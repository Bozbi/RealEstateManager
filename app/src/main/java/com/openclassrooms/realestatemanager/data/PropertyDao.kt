package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties")
    fun getAllProperties(): LiveData<List<Property>>

    @Insert
    fun insertProperty(property: Property)

    @Update
    fun updateProperty(property: Property)
}