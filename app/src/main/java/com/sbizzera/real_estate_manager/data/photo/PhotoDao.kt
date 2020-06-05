package com.sbizzera.real_estate_manager.data.photo

import androidx.room.*

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos WHERE propertyId LIKE :propertyId")
    suspend fun getAllPhotosByPropertyId(propertyId:String):List<Photo>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo)

}