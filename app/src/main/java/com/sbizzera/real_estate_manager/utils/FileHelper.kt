package com.sbizzera.real_estate_manager.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.photo.Photo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileHelper private constructor() {

    companion object {
        val instance: FileHelper by lazy { FileHelper() }
    }

    private val appContext = App.instance
    private val tempStorage = File("${appContext.filesDir}/temp")

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

    fun saveImageToPropertyFolder(path: String?, propertyId: String, photoId: String): String {
        var fileToCopy = File(path!!)
        if (!fileToCopy.exists()) {
            fileToCopy = File(
                tempStorage,
                appContext.contentResolver.getFileName(Uri.parse(path))
            )
        }

        val bmpOptions = BitmapFactory.Options()
        var bitmap = BitmapFactory.decodeFile(fileToCopy.absolutePath, bmpOptions)

        val fileDir = File("${appContext.filesDir}/$propertyId")
        fileDir.mkdir()
        val newFileName = "${photoId}.jpg"
        val newFile = File(fileDir, newFileName)


        val fOut = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut)
        fOut.close()
        fOut.flush()


        return Uri.fromFile(newFile).toString()
    }

    fun fileExistsInPropertyFolder(photoId: String, propertyId: String): Boolean {
        val file = File("${appContext.filesDir}/$propertyId/$photoId.jpg")
        return file.exists()
    }

    fun getUriFromFileName(fileName: String, propertyId: String): String {
        return "${appContext.filesDir}/$propertyId/$fileName.jpg"
    }

    fun deleteOldPhotosFromPropertyDirectory(propertyId: String, photoList: List<Photo>) {
        val dir = File("${appContext.filesDir}/${propertyId}")
        val files = dir.listFiles()
        files?.forEach { file ->
            val name = file.name.replace(".jpg", "")
            val size = photoList.filter { photo ->
                photo.photoId == name
            }.size
            if (size == 0) {
                file.delete()
            }
        }
    }

    fun deleteCache() {
        tempStorage.deleteRecursively()
    }

    fun createEmptyFileToReceiveRemoteImage(propertyId: String, fileName: String): File {

        val fileDir = File("${appContext.filesDir}/$propertyId")
        if (!fileDir.exists()) {
            fileDir.mkdir()
        }
        return File("${appContext.filesDir}/$propertyId/$fileName.jpg")
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