package com.sbizzera.real_estate_manager.ui.rem_activity

import com.sbizzera.real_estate_manager.data.FirebaseStorageRepository
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.utils.FileHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
                        propertyRepository.insertLocalProperty(remoteProperty)
                    }
                }
            } else {
                CoroutineScope(IO).launch {
                    propertyRepository.insertLocalProperty(remoteProperty)
                }
            }
        }
    }

    //TODO idem up
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
                    val photoIdListToRemove =
                        getPhotoListToRemove(localProperty.photoList, remotePropertyToWorkOn[0].photoList)
                    val photosIdListToAdd = getPhotoListToAdd(localProperty.photoList, remotePropertyToWorkOn[0].photoList)
                    photoIdListToRemove.forEach {

                    }
                    photosIdListToAdd.forEach {
                        CoroutineScope(IO).launch {
                            firebaseStorageRepository.uploadImage(fileHelper.getUriFromFileName(it,localProperty.propertyId))
                        }
                    }
                    CoroutineScope(IO).launch {
                        propertyRepository.insertRemoteProperty(localProperty)
                    }
                }
            } else {
                val photoIdListToRemove =
                    getPhotoListToRemove(localProperty.photoList, remotePropertyToWorkOn[0].photoList)
                val photosIdListToAdd = getPhotoListToAdd(localProperty.photoList, remotePropertyToWorkOn[0].photoList)
                photoIdListToRemove.forEach {

                }
                photosIdListToAdd.forEach {
                    CoroutineScope(IO).launch {
                        firebaseStorageRepository.uploadImage(fileHelper.getUriFromFileName(it,localProperty.propertyId))
                    }
                }
                CoroutineScope(IO).launch {
                    //Todo add same things than up or factorise
                    propertyRepository.insertRemoteProperty(localProperty)
                }
            }
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