package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

interface PropertyPhotoDao {
    @Insert
    fun insertPhoto(propertyPhoto: PropertyPhoto)

    @Delete
    fun deletePhoto(propertyPhoto: PropertyPhoto)

    @Query("SELECT * FROM property_photo WHERE id like :propertyId")
    fun getAllPhotosForProperty(propertyId: Long) : LiveData<List<PropertyPhoto>>
}