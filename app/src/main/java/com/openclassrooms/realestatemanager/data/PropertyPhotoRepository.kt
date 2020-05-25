package com.openclassrooms.realestatemanager.data

class PropertyPhotoRepository(
    private val propertyPhotoDao: PropertyPhotoDao
) {
    companion object {
        private var instance: PropertyPhotoRepository? = null

        fun getInstance(propertyPhotoDao: PropertyPhotoDao)=
            instance ?: PropertyPhotoRepository(propertyPhotoDao).also { instance = it }
    }
}