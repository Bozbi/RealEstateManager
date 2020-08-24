package com.sbizzera.real_estate_manager.data.api

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.sbizzera.real_estate_manager.data.model.Property

@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties")
    fun getAllProperties(): LiveData<List<Property>>

    @Query("SELECT * FROM properties WHERE propertyId LIKE :propertyId")
    fun getPropertyWithCursor(propertyId: String) : Cursor

    @Query("SELECT*FROM properties")
    suspend fun getAllPropertiesAsync(): List<Property>

    @Insert(onConflict = REPLACE)
    suspend fun insertProperty(property: Property):Long

    @Insert(onConflict = REPLACE)
    fun insertPropertyForTest(property: Property):Long

    @Query("SELECT * FROM properties WHERE propertyId LIKE :propertyId LIMIT 1")
    fun getPropertyByIdLD(propertyId: String): LiveData<Property>

    @Query("SELECT * FROM properties WHERE propertyId LIKE :propertyId LIMIT 1")
    suspend fun getPropertyById(propertyId: String): Property

    @Query("SELECT * FROM properties WHERE propertyId LIKE :propertyId LIMIT 1")
    suspend fun getPropertyByIdAsync(propertyId: String): Property

    @Query("DELETE FROM properties WHERE propertyId LIKE :propertyId")
    fun deleteTables(propertyId: String)

}