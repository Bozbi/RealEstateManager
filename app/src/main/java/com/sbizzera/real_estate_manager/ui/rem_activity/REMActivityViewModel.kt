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

class REMActivityViewModel(
    private val propertyRepository: PropertyRepository,
    private val fileHelper: FileHelper,
    private val appContext: App
) : ViewModel() {

    companion object {
        private const val REQUEST_IMAGE_FROM_GALLERY: Int = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }


    private lateinit var currentTempPhotoUri: String
    val viewAction = SingleLiveEvent<ViewAction>()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let {
                currentTempPhotoUri = fileHelper.createTempPhotoFileFromUriAndGetPathBack(it)
                viewAction.value = ViewAction.OnPhotoSelected(currentTempPhotoUri)
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            viewAction.value = ViewAction.OnPhotoSelected(currentTempPhotoUri)
        }
    }


    fun onLaunchGalleryAsked() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        viewAction.value = ViewAction.LaunchGallery(intent, REQUEST_IMAGE_FROM_GALLERY)
    }

    fun onLaunchCameraAsked() {
        val takePictureIntent2 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent2.resolveActivity(appContext.packageManager)
        currentTempPhotoUri = fileHelper.createEmptyTempPhotoFileAndGetUriBack()
        takePictureIntent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(currentTempPhotoUri))
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            takePictureIntent2.clipData = ClipData.newRawUri("", Uri.parse(currentTempPhotoUri))
            takePictureIntent2.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        viewAction.value = ViewAction.LaunchCamera(takePictureIntent2, REQUEST_IMAGE_CAPTURE)
    }

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
        data class LaunchCamera(val intent: Intent, val requestCode: Int) : ViewAction()
        data class LaunchGallery(val intent: Intent, val requestCode: Int) : ViewAction()
        data class OnPhotoSelected(val photoUri: String) : ViewAction()
        object LaunchPhotoEditor :ViewAction()
        object LaunchDetails : ViewAction()
        object LaunchEditProperty : ViewAction()
    }

}



