package com.sbizzera.real_estate_manager.ui.rem_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class REMActivityViewModel(
    private val synchroniseDataHelper: SynchroniseDataHelper
) : ViewModel() {

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

    fun syncLocalAndRemoteData() {
        viewModelScope.launch(IO) {
            synchroniseDataHelper.synchroniseData()
        }
    }


    sealed class ViewAction {
        object LaunchPhotoEditor : ViewAction()
        object LaunchDetails : ViewAction()
        object LaunchEditProperty : ViewAction()
        object LaunchMap : ViewAction()
    }

}



