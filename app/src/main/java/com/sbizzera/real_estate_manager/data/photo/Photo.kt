package com.sbizzera.real_estate_manager.data.photo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo (
    @PrimaryKey
    val photoId:String,
    val title : String,
    val uri : String,
    val description : String,
    val propertyId : String
)
