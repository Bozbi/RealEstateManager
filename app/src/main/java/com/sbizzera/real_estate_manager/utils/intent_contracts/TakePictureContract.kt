package com.sbizzera.real_estate_manager.utils.intent_contracts

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.sbizzera.real_estate_manager.utils.FileHelper

class TakePictureContract (fileHelper: FileHelper) : ActivityResultContract<String,String>() {

    private val currentTempPhotoUri = Uri.parse(fileHelper.createEmptyTempPhotoFileAndGetUriBack())

    override fun createIntent(context: Context, input: String?): Intent {
        val takePictureIntent2 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent2.resolveActivity(context.packageManager)
        takePictureIntent2.putExtra(MediaStore.EXTRA_OUTPUT, currentTempPhotoUri)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            takePictureIntent2.clipData = ClipData.newRawUri("", currentTempPhotoUri)
            takePictureIntent2.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return takePictureIntent2
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when (resultCode){
            Activity.RESULT_OK -> {
                currentTempPhotoUri.toString()
            }
            else -> null
        }
    }
}