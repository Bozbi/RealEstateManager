package com.sbizzera.real_estate_manager.data.property

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.AppLocalDatabase
import kotlinx.coroutines.tasks.await

class PropertyRepository private constructor() {

    private val propertyDao = AppLocalDatabase.getInstance(App.instance).propertyDao()
    private val firestoreProperties = Firebase.firestore.collection("properties")

    companion object {
        val instance: PropertyRepository by lazy { PropertyRepository() }
    }

    suspend fun insertLocalProperty(property: Property) = propertyDao.insertProperty(property)

    fun getAllLocalProperties() = propertyDao.getAllProperties()

    suspend fun getAllPropertiesAsync() = propertyDao.getAllPropertiesAsync()

    fun getPropertyByIdLD(propertyId: String) :LiveData<Property> {
        return propertyDao.getPropertyByIdLD(propertyId)
    }

    suspend fun getPropertyById(propertyId: String):Property{
        return propertyDao.getPropertyById(propertyId)
    }

    suspend fun insertRemoteProperty(property: Property) {
        firestoreProperties.document(property.propertyId).set(property).await()
    }

    suspend fun getAllRemoteProperties():List<Property> =firestoreProperties.get().await().toObjects(Property::class.java)

}

