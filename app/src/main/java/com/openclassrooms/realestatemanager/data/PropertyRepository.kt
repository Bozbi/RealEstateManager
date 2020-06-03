package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.App

object PropertyRepository{

    private val propertyDao = AppDatabase.getInstance(App.getInstance()).propertyDao()

    suspend fun insertProperty(property: Property)= propertyDao.insertProperty(property)

    suspend fun getAllProperties() = propertyDao.getAllProperties()

}

