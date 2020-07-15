package com.sbizzera.real_estate_manager.utils.intent_contracts

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.sbizzera.real_estate_manager.utils.FileHelper

class TakePictureFromGalleryContract(private val fileHelper: FileHelper) : ActivityResultContract<String, String>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when (resultCode) {
            RESULT_OK -> {
                intent?.data?.let {
                    val currentTempPhotoUri = fileHelper.createTempPhotoFileFromUriAndGetPathBack(it)
                    currentTempPhotoUri
                }
            }
            else -> null
        }
    }
}