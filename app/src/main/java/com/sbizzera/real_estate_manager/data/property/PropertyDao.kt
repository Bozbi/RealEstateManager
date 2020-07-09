package com.sbizzera.real_estate_manager.data.property

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties")
    fun getAllProperties(): LiveData<List<Property>>

    @Insert(onConflict = REPLACE)
    fun insertProperty(property: Property):Long

    @Query("SELECT * FROM properties WHERE propertyId LIKE :propertyId")
    fun getPropertyById(propertyId: String): LiveData<Property>
}