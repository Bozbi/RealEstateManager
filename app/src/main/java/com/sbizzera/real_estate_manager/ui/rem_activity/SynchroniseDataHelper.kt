package com.sbizzera.real_estate_manager.ui.rem_activity

import android.content.BroadcastReceiver
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.impl.utils.ForceStopRunnable
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.FirebaseStorageRepository
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.utils.FileHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime

class SynchroniseDataHelper(
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val fileHelper: FileHelper,
    private val propertyRepository: PropertyRepository
) {

    suspend fun synchroniseData() {
        val remoteProperties = propertyRepository.getAllRemoteProperties()
        val localProperties = propertyRepository.getAllPropertiesAsync()
        updateLocalProperties(remoteProperties, localProperties)
        updateRemoteProperties(remoteProperties, localProperties)
    }

    //TODO why can't i make this a suspend function
    private fun updateLocalProperties(remoteProperties: List<Property>, localProperties: List<Property>) {
        remoteProperties.forEach { remoteProperty ->
            val localPropertyToWorkOn = localProperties.filter { localProperty ->
                remoteProperty.propertyId == localProperty.propertyId
            }
            if (!localPropertyToWorkOn.isNullOrEmpty()) {
                val isRemoteModificationDateMoreRecent =
                    LocalDateTime.parse(remoteProperty.modificationDate)
                        .isAfter(LocalDateTime.parse(localPropertyToWorkOn[0].modificationDate))
                if (isRemoteModificationDateMoreRecent) {
                    CoroutineScope(IO).launch {
                        updateExistingLocalPropertyAndPhoto(localPropertyToWorkOn[0], remoteProperty)
                    }
                }
            } else {
                CoroutineScope(IO).launch {
                    insertNewLocalPropertyAndPhoto(remoteProperty)
                }
            }
        }
    }

    private fun insertNewLocalPropertyAndPhoto(remoteProperty: Property) {
        CoroutineScope(IO).launch {
            val photosIdListToAdd = mutableListOf<String>()
            remoteProperty.photoList.forEach {
                photosIdListToAdd.add(it.photoId)
            }
            photosIdListToAdd.forEach { name ->
                val file = fileHelper.createEmptyFileToReceiveRemoteImage(remoteProperty.propertyId, name)
                firebaseStorageRepository.downloadImage(name, file)
            }
            withContext(Main) {
                propertyRepository.insertLocalProperty(remoteProperty)
            }
        }
    }

    private fun updateExistingLocalPropertyAndPhoto(localProperty: Property, remoteProperty: Property) {
        CoroutineScope(IO).launch {
            val photosIdListToAdd = getPhotoListToAdd(remoteProperty.photoList, localProperty.photoList)
            photosIdListToAdd.forEach { name ->
                val file = fileHelper.createEmptyFileToReceiveRemoteImage(remoteProperty.propertyId, name)
                firebaseStorageRepository.downloadImage(name, file)
            }
            withContext(Main) {
                propertyRepository.insertLocalProperty(remoteProperty)
            }
            fileHelper.deleteOldPhotosFromPropertyDirectory(localProperty.propertyId, localProperty.photoList)
        }
    }

    //TODO idem up (why not a suspend function)
    private fun updateRemoteProperties(remoteProperties: List<Property>, localProperties: List<Property>) {
        localProperties.forEach { localProperty ->
            val remotePropertyToWorkOn = remoteProperties.filter { remoteProperty ->
                remoteProperty.propertyId == localProperty.propertyId
            }
            if (!remotePropertyToWorkOn.isNullOrEmpty()) {
                val isLocalModificationDateMoreRecent =
                    LocalDateTime.parse(localProperty.modificationDate)
                        .isAfter(LocalDateTime.parse(remotePropertyToWorkOn[0].modificationDate))
                if (isLocalModificationDateMoreRecent) {
                    updateExistingRemotePropertyAndPhoto(localProperty, remotePropertyToWorkOn[0])
                }
            } else {
                insertNewRemotePropertyAndPhoto(localProperty)
            }
        }
    }

    private fun insertNewRemotePropertyAndPhoto(localProperty: Property) {
        CoroutineScope(IO).launch {
            val photosIdListToAdd = mutableListOf<String>()
            localProperty.photoList.forEach {
                photosIdListToAdd.add(it.photoId)
            }
            photosIdListToAdd.forEach {
                firebaseStorageRepository.uploadImage(fileHelper.getUriFromFileName(it, localProperty.propertyId))
            }
            propertyRepository.insertRemoteProperty(localProperty)
        }
    }

    private fun updateExistingRemotePropertyAndPhoto(
        localProperty: Property,
        remoteProperty: Property
    ) {
        CoroutineScope(IO).launch {
            val photoIdListToRemove =
                getPhotoListToRemove(localProperty.photoList, remoteProperty.photoList)
            val photosIdListToAdd = getPhotoListToAdd(localProperty.photoList, remoteProperty.photoList)
            photoIdListToRemove.forEach {
                firebaseStorageRepository.deleteImage(it)
            }
            photosIdListToAdd.forEach {
                firebaseStorageRepository.uploadImage(fileHelper.getUriFromFileName(it, localProperty.propertyId))
            }
            propertyRepository.insertRemoteProperty(localProperty)
        }
    }

    private fun getPhotoListToAdd(newPhotoList: List<Photo>, oldPhotoList: List<Photo>): List<String> {
        val listToAdd = mutableListOf<String>()
        newPhotoList.forEach { newPhoto ->
            val photoId = oldPhotoList.filter { oldPhoto ->
                newPhoto.photoId == oldPhoto.photoId
            }
            if (photoId.isEmpty()) {
                listToAdd.add(newPhoto.photoId)
            }
        }
        return listToAdd
    }

    private fun getPhotoListToRemove(newPhotoList: List<Photo>, oldPhotoList: List<Photo>): List<String> {
        val listToRemove = mutableListOf<String>()
        oldPhotoList.forEach { oldPhoto ->
            val photoId = newPhotoList.filter { newPhoto ->
                oldPhoto.photoId == newPhoto.photoId
            }
            if (photoId.isEmpty()) {
                listToRemove.add(oldPhoto.photoId)
            }
        }
        return listToRemove
    }
}