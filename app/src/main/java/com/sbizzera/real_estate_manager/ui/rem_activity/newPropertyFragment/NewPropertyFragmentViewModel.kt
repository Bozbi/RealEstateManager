package com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.ViewAction.*
import com.sbizzera.real_estate_manager.utils.FAKE_PHOTO_LIST
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class NewPropertyFragmentViewModel(

) : ViewModel() {
    private var _UiModelLD = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> = _UiModelLD

    var viewAction = SingleLiveEvent<ViewAction>()

    var photoEditorViewAction = SingleLiveEvent<ViewAction>()

    private var photoList: MutableList<Photo> = FAKE_PHOTO_LIST

    private var _CurrentPhotoEditedLD = MutableLiveData<Pair<Photo, Int>>()
    val currentPhotoEditedLD: LiveData<Pair<Photo, Int>> = _CurrentPhotoEditedLD


    init {
        _UiModelLD.value = UiModel(photoList, photoList[0].title)
    }

    fun onRecyclerScroll(newState: Int, recyclerPosition: Int) {
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
//                var title = photoList[recyclerPosition].title
//                if (title.isNullOrEmpty()) {
//                    title = "add a new photo"
//                }
//                _UiModelLD.value = _UiModelLD.value?.copy(currentPhotoTitle = title)
                viewAction.value = ShowPhotoTitle
                if (recyclerPosition == photoList.size - 1) {
                    viewAction.value = HideEditPhoto
                    viewAction.value = ShowAddPhoto
                } else {
                    viewAction.value = HideAddPhoto
                    viewAction.value = ShowEditPhoto
                }
            }

            RecyclerView.SCROLL_STATE_DRAGGING -> {
                viewAction.value = HidePhotoTitle
                viewAction.value = ClosePhotoMenu
                viewAction.value = HideAddPhoto
                viewAction.value = HideEditPhoto
            }
        }
    }

    fun addPhotoClicked(alpha: Float) {
        val menuIsOpen = alpha > 0
        viewAction.value = if (menuIsOpen) ClosePhotoMenu else OpenPhotoMenu
    }

    fun takePhotoFromCameraClicked() {
        viewAction.value = TakePhotoFromCamera
    }

    fun onPhotoSelected(uri: String, recyclerPosition: Int) {
        val photo = photoList[recyclerPosition].copy(uri = uri)
        _CurrentPhotoEditedLD.value = Pair(photo, recyclerPosition)
        viewAction.value = LaunchEditor

    }

    fun takePhotoFromGalleryClicked() {
        viewAction.value = TakePhotoFromGallery
    }

    fun editPhotoClicked(recyclerPosition: Int) {
        _CurrentPhotoEditedLD.value = Pair(photoList[recyclerPosition], recyclerPosition)
        viewAction.value = LaunchEditor
    }

    fun onSaveButtonClicked(title: String) {
        if (title.isNotEmpty()) {
            viewAction.value = ClosePhotoMenu
            val currentPosition: Int? = _CurrentPhotoEditedLD.value?.second
            val photoToInsert = _CurrentPhotoEditedLD.value?.first?.copy(title = title)
            photoList.removeAt(currentPosition!!)
            photoList.add(currentPosition, photoToInsert!!)
            _UiModelLD.value = UiModel(photoList, photoToInsert.title)
            photoEditorViewAction.value = CloseFragment
            viewAction.value = ShowEditPhoto

        }else{
            photoEditorViewAction.value = TitleEmptyError
        }
    }

    fun onDeleteButtonClicked() {
        val currentPosition: Int? = _CurrentPhotoEditedLD.value?.second
        photoList.removeAt(currentPosition!!)
        _UiModelLD.value = UiModel(photoList,photoList[currentPosition].title)
        photoEditorViewAction.value = CloseFragment
    }



    sealed class ViewAction {
        object ShowPhotoTitle : ViewAction()
        object HidePhotoTitle : ViewAction()
        object HideAddPhoto : ViewAction()
        object ShowAddPhoto : ViewAction()
        object OpenPhotoMenu : ViewAction()
        object ClosePhotoMenu : ViewAction()
        object TakePhotoFromCamera : ViewAction()
        object TakePhotoFromGallery : ViewAction()
        object HideEditPhoto : ViewAction()
        object ShowEditPhoto : ViewAction()
        object LaunchEditor : ViewAction()
        object CloseFragment : ViewAction()
        object TitleEmptyError : ViewAction()
    }

}

data class UiModel(
    val photoList: List<Photo>,
    val currentPhotoTitle: String
)