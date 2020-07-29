package com.sbizzera.real_estate_manager.ui.rem_activity

import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class REMActivityViewModel : ViewModel() {

    val viewAction = SingleLiveEvent<ViewAction>()


    fun onPhotoEditorAsked() {
        viewAction.value = ViewAction.LaunchPhotoEditor
    }

    fun onPropertyDetailsAsked() {
        viewAction.value = ViewAction.LaunchDetails
    }

    fun onAddOrModifyPropertyAsked() {
        viewAction.value = ViewAction.LaunchEditProperty
    }

    fun onMapAsked() {
        viewAction.value = ViewAction.LaunchMap
    }

    fun onRationalPermissionAsked() {
        viewAction.value = ViewAction.LaunchRationalPermissionDialog
    }


    sealed class ViewAction {
        object LaunchPhotoEditor : ViewAction()
        object LaunchDetails : ViewAction()
        object LaunchEditProperty : ViewAction()
        object LaunchMap : ViewAction()
        object LaunchRationalPermissionDialog : ViewAction()
    }

}



