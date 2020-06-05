package com.sbizzera.real_estate_manager.data.property

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sbizzera.real_estate_manager.data.property.Property


@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties")
    suspend fun getAllProperties(): List<Property>

    @Insert
    suspend fun insertProperty(property: Property)
}