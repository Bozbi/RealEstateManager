package com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.repository.CurrentPhotoPositionRepo
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.model.Photo
import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.data.repository.PropertyRepository
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import com.sbizzera.real_estate_manager.utils.architecture_components.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoViewerViewModel(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    propertyRepository: PropertyRepository,
    private val currentPhotoPositionRepo: CurrentPhotoPositionRepo,
    private val fileHelper: FileHelper
) : ViewModel() {

    var photoList = MutableLiveData<List<PhotoInViewer>>()
    val photoViewerViewAction =
        SingleLiveEvent<PhotoViewerViewAction>()

    init {
        val id = currentPropertyIdRepository.currentPropertyIdLiveData.value
        if (id != null) {
            viewModelScope.launch(IO) {
                val property = propertyRepository.getPropertyById(id)
                withContext(Main) {
                    photoList.value = fromListPhotoToListPhotoInViewer(property)
                }
            }
        }
    }

    fun getCurrentPhotoPosition() = currentPhotoPositionRepo.currentPhotoPosition

    private fun fromListPhotoToListPhotoInViewer(property: Property): List<PhotoInViewer> {
        val listToReturn = mutableListOf<PhotoInViewer>()
        property.photoList.forEach {
            val photoInViewer = fromPhotoToPhotoInViewer(it, property.propertyId)
            listToReturn.add(photoInViewer)
        }
        return listToReturn
    }

    private fun fromPhotoToPhotoInViewer(photo: Photo, propertyId: String): PhotoInViewer {
        return PhotoInViewer(
            fileHelper.getUriFromFileName(photo.photoId, propertyId),
            photo.title
        )
    }

    fun onViewHolderBound(position: Int) {
        if (position == currentPhotoPositionRepo.currentPhotoPosition) {
            photoViewerViewAction.value = PhotoViewerViewAction.ViewHolderReady
        }
    }

    fun setCurrentPhotoPosition(currentPhotoPosition: Int) {
        currentPhotoPositionRepo.currentPhotoPosition = currentPhotoPosition
    }

    sealed class PhotoViewerViewAction {
        object ViewHolderReady : PhotoViewerViewAction()
    }
}

data class PhotoInViewer(
    val uri: String,
    val title: String
)