package com.sbizzera.real_estate_manager.ui.rem_activity

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.data.property_register.PropertyRegisterRepository
import com.sbizzera.real_estate_manager.data.utils.toPropertyRegisterRow
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.FAKE_PROPERTIES
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class REMActivityViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyRegisterRepository: PropertyRegisterRepository,
    private val fileHelper: FileHelper,
    private val appContext: App
) : ViewModel() {

    companion object {
        private const val REQUEST_IMAGE_FROM_GALLERY: Int = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }


    lateinit var currentTempPhotoUri: String

    val viewAction = SingleLiveEvent<ViewAction>()

    init {
//        populateDbForTesting()
//        compareRegistersAndReact()
    }



    private fun insertProperty(propertyToInsert: Property) {
        viewModelScope.launch {
            propertyRepository.insertLocalProperty(propertyToInsert)
            propertyRegisterRepository.insertPropertyInLocalRegister(propertyToInsert.toPropertyRegisterRow())
        }
    }


    private fun populateDbForTesting() {
        val propertyList = FAKE_PROPERTIES
        propertyList.forEach {
            insertProperty(it)
        }
    }

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


    fun onLaunchGalleryClick() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        viewAction.value = ViewAction.OnLaunchGalleryClick(intent, REQUEST_IMAGE_FROM_GALLERY)
    }

    fun onLaunchCameraClick() {
        val takePictureIntent2 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent2.resolveActivity(appContext.packageManager)
        currentTempPhotoUri = fileHelper.createEmptyTempPhotoFileAndGetUriBack()
        takePictureIntent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(currentTempPhotoUri))
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            takePictureIntent2.clipData = ClipData.newRawUri("", Uri.parse(currentTempPhotoUri))
            takePictureIntent2.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        viewAction.value = ViewAction.OnLaunchCameraClick(takePictureIntent2, REQUEST_IMAGE_CAPTURE)
    }

    sealed class ViewAction {
        data class OnLaunchCameraClick(val intent: Intent, val requestCode: Int) : ViewAction()
        data class OnLaunchGalleryClick(val intent: Intent, val requestCode: Int) : ViewAction()
        data class OnPhotoSelected(val photoUri: String) : ViewAction()
    }

}



