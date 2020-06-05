package com.sbizzera.real_estate_manager.ui.rem_activity.photoFragment

import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class PhotoFragmentViewModel : ViewModel() {

    private var _viewAction: SingleLiveEvent<ViewAction> = SingleLiveEvent()
    val viewAction = _viewAction


    fun savePhotoBtnClicked() {
        _viewAction.value = ViewAction.SavePhoto()
    }

    fun addPhotoFromGalleryBtnClicked() {
        _viewAction.value = ViewAction.LaunchGallery()
    }

    fun takePhotoBtnClicked() {
        _viewAction.value = ViewAction.LaunchCamera()
    }

}

sealed class ViewAction{
    class SavePhoto : ViewAction()
    class LaunchGallery : ViewAction()
    class LaunchCamera : ViewAction()
}