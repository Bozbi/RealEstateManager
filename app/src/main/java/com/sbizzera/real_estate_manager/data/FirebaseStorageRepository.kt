package com.sbizzera.real_estate_manager.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseStorageRepository private constructor() {

    companion object {
        val instance: FirebaseStorageRepository by lazy { FirebaseStorageRepository() }
    }

    val storageRef = FirebaseStorage.getInstance().reference

    suspend fun uploadImage(uri: String) {
        var file = Uri.fromFile(File(uri))
        val storageChild = storageRef.child("${file.lastPathSegment}")
        storageChild.putFile(file).await()
    }
}