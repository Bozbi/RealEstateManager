package com.sbizzera.real_estate_manager.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object FileHelper {

    private val appContext = App.getInstance()
    private val tempStorage = File("${ appContext.filesDir}/temp")

    fun createEmptyTempPhotoFileAndGetUriBack(): String {
        val file =
            createImageFile()
        return FileProvider.getUriForFile(
            appContext,
            appContext.getString(R.string.app_package_name), file
        ).toString()
    }

    private fun createImageFile(): File {
        tempStorage.mkdir()
        return File.createTempFile(
            "temp_",
            ".jpg",
            tempStorage
        )
    }

    fun createTempPhotoFileFromUriAndGetPathBack(uri: Uri?): String {
        val parcelFileDescriptor =
            appContext.contentResolver.openFileDescriptor(uri!!, "r", null)
        if (parcelFileDescriptor != null) {
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            tempStorage.mkdir()
            val file = File(
                tempStorage,
                appContext.contentResolver.getFileName(uri)
            )
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            return file.absolutePath
        }
        return ""
    }

    fun saveImageToPropertyFolder(path: String?, propertyId: String,photoId:String): String {
        var fileToCopy = File(path!!)
        if (!fileToCopy.exists()) {
            fileToCopy = File(
                tempStorage,
                appContext.contentResolver.getFileName(Uri.parse(path))
            )
        }
        val fileDir = File("${appContext.filesDir}/$propertyId")
        fileDir.mkdir()
        val newFileName = "${photoId}.jpg"
        val newFile = File(fileDir, newFileName)
        fileToCopy.copyTo(newFile, true)
        return Uri.fromFile(newFile).toString()
    }

    fun fileExists(uri: String): Boolean {
        val file = File(appContext.contentResolver.getFileName(Uri.parse(uri)))
        return file.exists()
    }

    fun deleteCache() {
        tempStorage.deleteRecursively()
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