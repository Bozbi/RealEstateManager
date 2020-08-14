package com.sbizzera.real_estate_manager.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseStorageRepository private constructor() {

    companion object {
        val instance: FirebaseStorageRepository by lazy { FirebaseStorageRepository() }
    }

    private val storageRef = FirebaseStorage.getInstance().reference

    suspend fun uploadImage(uri: String) {
        val file = Uri.fromFile(File(uri))
        val storageChild = storageRef.child("${file.lastPathSegment}")
        storageChild.putFile(file).await()
    }

    suspend fun deleteImage(name: String) {
        storageRef.child("$name.jpg").delete().await()
    }

    fun downloadImage(name: String,file:File) {
        storageRef.child("$name.jpg").getFile(file)
    }
}