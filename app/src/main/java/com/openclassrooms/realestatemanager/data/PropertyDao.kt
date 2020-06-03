package com.openclassrooms.realestatemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties")
    suspend fun getAllProperties(): List<Property>

    @Insert
    suspend fun insertProperty(property: Property)
}