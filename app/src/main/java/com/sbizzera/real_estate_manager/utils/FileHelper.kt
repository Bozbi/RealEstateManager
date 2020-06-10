package com.sbizzera.real_estate_manager.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object FileHelper {
    private val appContext = App.getInstance()

    fun createEmptyTempPhotoFileAndGetUriBack(): String {
        val file =
            createImageFile()
        return FileProvider.getUriForFile(
            appContext,
            appContext.getString(R.string.app_package_name), file
        ).toString()
    }

    private fun createImageFile(): File {
        val storageDir: File? = appContext.filesDir
        return File.createTempFile(
            "temp",
            ".jpg",
            storageDir
        )
    }

    fun createTempPhotoFileFromUriAndGetPathBack(uri: Uri?): String {
        val parcelFileDescriptor =
            appContext.contentResolver.openFileDescriptor(uri!!, "r", null)
        if (parcelFileDescriptor != null) {
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(
                appContext.filesDir,
                appContext.contentResolver.getFileName(uri)
            )
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            return file.absolutePath
        }
        return ""
    }

    fun saveImageToPropertyFolder(path: String?, propertyId: String): String {
        var fileToCopy = File(path!!)
        if (!fileToCopy.exists()) {
            fileToCopy = File(
                appContext.filesDir,
                appContext.contentResolver.getFileName(Uri.parse(path))
            )
        }
        val fileDir = File("${appContext.filesDir}/$propertyId")
        fileDir.mkdir()
//        val newFile = File.createTempFile(
//            "photo_",
//            ".jpg",
//            fileDir
//        )
        val newFileName = "${UUID.randomUUID().toString()}.jpg"
        val newFile = File(fileDir,newFileName)
        fileToCopy.copyTo(newFile, true)
        return newFile.path
    }
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}