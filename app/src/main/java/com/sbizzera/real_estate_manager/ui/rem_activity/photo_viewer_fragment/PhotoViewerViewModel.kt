package com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer_fragment

import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.data.CurrentPhotoPositionRepo
import com.sbizzera.real_estate_manager.data.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class PhotoViewerViewModel(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    propertyRepository: PropertyRepository,
    private val currentPhotoPositionRepo: CurrentPhotoPositionRepo,
    private val fileHelper: FileHelper
) : ViewModel() {
    var photoList = listOf<PhotoInViewer>()
    val photoViewerViewAction = SingleLiveEvent<PhotoViewerViewAction>()

    init {
        val id = currentPropertyIdRepository.currentPropertyIdLiveData.value
        if (id != null) {
            val property = propertyRepository.getPropertyById(id)
            photoList = fromListPhotoToListPhotoInViewer(property)
        }
    }

    fun getCurrentPhotoPosition()= currentPhotoPositionRepo.currentPhotoPosition

    private fun fromListPhotoToListPhotoInViewer(property: Property): List<PhotoInViewer> {
        val listToReturn = mutableListOf<PhotoInViewer>()
        property.photoList.forEach {
            val photoInViewer = fromPhotoToPhotoInViewer(it,property.propertyId)
            listToReturn.add(photoInViewer)
        }
        return listToReturn
    }

    private fun fromPhotoToPhotoInViewer(photo: Photo,propertyId: String): PhotoInViewer {
        return PhotoInViewer(
            fileHelper.getUriFromFileName(photo.photoId,propertyId),
            photo.title
        )
    }

    fun onViewHolderBound(position: Int) {
        if(position == currentPhotoPositionRepo.currentPhotoPosition){
            photoViewerViewAction.value= PhotoViewerViewAction.ViewHolderReady
        }
    }

    fun setCurrentPhotoPosition(currentPhotoPosition: Int) {
        currentPhotoPositionRepo.currentPhotoPosition = currentPhotoPosition
    }

    sealed class PhotoViewerViewAction{
        object  ViewHolderReady :  PhotoViewerViewAction()
    }
}

data class PhotoInViewer(
    val uri: String,
    val title : String
)