package com.sbizzera.real_estate_manager.data.property

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.AppLocalDatabase
import kotlinx.coroutines.tasks.await

object PropertyRepository {

    private val propertyDao = AppLocalDatabase.getInstance(App.getInstance()).propertyDao()
    private val firestoreProperties = Firebase.firestore.collection("properties")

    fun insertLocalProperty(property: Property)=propertyDao.insertProperty(property)
    fun getAllLocalProperties() = propertyDao.getAllProperties()
    fun getPropertyById(propertyId: String) = propertyDao.getPropertyById(propertyId)

    suspend fun insertRemoteProperty(property: Property) {
        firestoreProperties.document(property.propertyId).set(property).await()
    }

}

