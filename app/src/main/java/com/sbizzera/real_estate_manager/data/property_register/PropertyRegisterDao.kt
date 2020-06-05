package com.sbizzera.real_estate_manager.data.property_register

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PropertyRegisterDao {

    @Query("SELECT * FROM property_register")
    suspend fun getAllRowsInPropertyRegister():List<PropertyRegisterRow>

    @Insert
    suspend fun insertPropertyInRegister(propertyRegisterRow : PropertyRegisterRow)
}