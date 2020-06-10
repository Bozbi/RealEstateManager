package com.sbizzera.real_estate_manager.ui.rem_activity.photoFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class PhotoFragmentViewModel(
    private val fileHelper: FileHelper
) : ViewModel() {

    private val _UiModelLiveData = MediatorLiveData<UiModel>()
    val uiModelLiveData: LiveData<UiModel>
        get() {
            return _UiModelLiveData
        }

    private val titleTxtLiveData = MutableLiveData<String>()
    private val currentPhotoUriLiveData = MutableLiveData<String>()

    private val _viewAction: SingleLiveEvent<ViewAction> = SingleLiveEvent()
    val viewAction = _viewAction

    private var propertyId: String = "testId"
//    private lateinit var propertyId : String

    init {
        wireUpMediator()
    }

    private fun wireUpMediator() {
        _UiModelLiveData.addSource(titleTxtLiveData) { titleTxt ->
            combineSources(titleTxt, currentPhotoUriLiveData.value)
        }
        _UiModelLiveData.addSource(currentPhotoUriLiveData) { currentPhotoUri ->
            combineSources(titleTxtLiveData.value, currentPhotoUri)
        }
    }

    private fun combineSources(titleText: String?, currentPhotoUri: String?) {
        val saveBtnIsClickable = !titleText.isNullOrEmpty() && currentPhotoUri != null
        _UiModelLiveData.value = UiModel(
            saveBtnIsClickable,
            currentPhotoUri
        )
    }


    fun onSavePhotoBtnClicked() {
        val path = fileHelper.saveImageToPropertyFolder(currentPhotoUriLiveData.value, propertyId)
        println("debug : path saved $path")
    }

    fun onTitleTextChanged(text: CharSequence?) {
        titleTxtLiveData.value = text.toString()
    }


    fun onPhotoSelected(uri: String) {
        currentPhotoUriLiveData.value = uri
    }

    fun onLaunchGalleryClick() {
        _viewAction.value = ViewAction.OnLaunchGalleryClick
    }

    fun onLaunchCameraClick() {
        _viewAction.value = ViewAction.OnLaunchCameraClick
    }


}

sealed class ViewAction {
    object OnLaunchGalleryClick : ViewAction()
    object OnLaunchCameraClick : ViewAction()
}

data class UiModel(
    val saveBtnClickable: Boolean = false,
    val imageUri: String?
)