package com.sbizzera.real_estate_manager.data.property_register

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.AppLocalDatabase
import kotlinx.coroutines.tasks.await

object PropertyRegisterRepository {
    private val propertyRegisterDao =
        AppLocalDatabase.getInstance(App.getInstance()).propertyRegisterDao()
    private val firestorePropertyRegister = Firebase.firestore.collection("propertyRegister")

    suspend fun insertPropertyInLocalRegister(propertyRegisterRow: PropertyRegisterRow) =
        propertyRegisterDao.insertPropertyInRegister(propertyRegisterRow)


    suspend fun updateRemoteRegister(register : HashMap<String,String>) =
        firestorePropertyRegister
            .document("register")
            .set(PropertyRegister(register))
            .await()


    suspend fun getLocalPropertyRegister() = propertyRegisterDao.getAllRowsInPropertyRegister()

    suspend fun getRemotePropertyRegister() =
        firestorePropertyRegister
            .document("register")
            .get()
            .await()
            .toObject<PropertyRegister>() ?: PropertyRegister(hashMapOf())
}