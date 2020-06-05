package com.sbizzera.real_estate_manager.data.property_register

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property_register")
data class PropertyRegisterRow (
    @PrimaryKey
    val propertyId : String,
    val lastModification : String
)