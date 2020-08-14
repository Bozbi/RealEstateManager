package com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor

import android.view.View
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.data.repository.CurrentEditedPhotoRepository
import com.sbizzera.real_estate_manager.data.repository.PropertyInModificationRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorViewModel.PhotoEditorViewAction.CloseFragment
import com.sbizzera.real_estate_manager.utils.architecture_components.SingleLiveEvent

class PhotoEditorViewModel(
    private val currentEditedPhotoRepository: CurrentEditedPhotoRepository,
    private val propertyInModificationRepository: PropertyInModificationRepository
) : ViewModel() {

    val photoEditorViewAction =
        SingleLiveEvent<PhotoEditorViewAction>()
    val currentPhotoEdited = currentEditedPhotoRepository.currentPhotoLD.value!!.first


    fun onDeletePhotoInEditor() {
        val currentPhotoList = propertyInModificationRepository.propertyInModificationLD.value!!.photoList
        val currentPhotoEdited = currentEditedPhotoRepository.currentPhotoLD.value!!.first
        currentPhotoList.remove(currentPhotoEdited)
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value!!.copy(photoList = currentPhotoList)
        currentEditedPhotoRepository.currentPhotoLD.value!!.second?.let {
            currentEditedPhotoRepository.currentPhotoLD.value ==null
        }
        photoEditorViewAction.value = CloseFragment
    }

    fun onSavePhotoInEditor(photoTitle: String) {
        if (photoTitle.isEmpty()) {
            photoEditorViewAction.value = PhotoEditorViewAction.TitleEmptyError
            return
        }
        if (photoTitle == currentPhotoEdited.photoTitle) {
            photoEditorViewAction.value = CloseFragment
            return
        }
        val currentPhotoList = propertyInModificationRepository.propertyInModificationLD.value!!.photoList
        if (currentPhotoEdited in currentPhotoList) {
            currentPhotoList.remove(currentPhotoEdited)
        }
        currentPhotoList.add(currentPhotoEdited.copy(photoTitle = photoTitle))
        var propertyInModification = propertyInModificationRepository.propertyInModificationLD.value!!
        propertyInModification =
            propertyInModification.copy(
                photoList = currentPhotoList,
                addPhotoVisibility = if (currentPhotoList.isEmpty()) View.VISIBLE else View.INVISIBLE
            )
        propertyInModificationRepository.propertyInModificationLD.value = propertyInModification
        if(currentEditedPhotoRepository.currentPhotoLD.value!!.second == null){
            currentEditedPhotoRepository.currentPhotoLD.value =
                currentEditedPhotoRepository.currentPhotoLD.value!!.copy(
                    second = currentPhotoList.size-1
                )
        }
        photoEditorViewAction.value = CloseFragment
    }


    sealed class PhotoEditorViewAction {
        object CloseFragment : PhotoEditorViewAction()
        object TitleEmptyError : PhotoEditorViewAction()
    }
}