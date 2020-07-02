package com.sbizzera.real_estate_manager.data.property

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties")
    suspend fun getAllProperties(): List<Property>

    @Insert(onConflict = REPLACE)
    suspend fun insertProperty(property: Property):Long
}