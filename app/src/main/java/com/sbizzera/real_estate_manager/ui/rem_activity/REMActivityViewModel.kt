package com.sbizzera.real_estate_manager.ui.rem_activity

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.property.PropertyRepository

import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class REMActivityViewModel: ViewModel() {

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


    sealed class ViewAction {
        object LaunchPhotoEditor :ViewAction()
        object LaunchDetails : ViewAction()
        object LaunchEditProperty : ViewAction()
    }

}



