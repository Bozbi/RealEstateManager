package com.sbizzera.real_estate_manager.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.File

object FirebaseStorageRepository {

    val storageRef = FirebaseStorage.getInstance().reference

    suspend fun uploadImage(uri:String){
        var file = Uri.fromFile(File(uri))
        println("debug : file exists? ${file}")
        val storageChild = storageRef.child("${file.lastPathSegment}")
        storageChild.putFile(Uri.parse(uri)).await()
    }
}