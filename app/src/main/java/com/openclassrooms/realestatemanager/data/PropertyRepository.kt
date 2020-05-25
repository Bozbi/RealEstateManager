package com.openclassrooms.realestatemanager.data

class PropertyRepository private constructor(
    private val propertyDao: PropertyDao
) {
    companion object {

        private var instance: PropertyRepository? = null

        fun getInstance(propertyDao: PropertyDao) =
            instance ?:PropertyRepository(propertyDao).also { instance = it }

    }
}