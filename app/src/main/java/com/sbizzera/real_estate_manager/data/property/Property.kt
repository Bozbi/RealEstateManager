package com.sbizzera.real_estate_manager.data.property

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.utils.Converters


@Entity(tableName = "properties")
data class Property(
    @PrimaryKey
    val propertyId: String = "",
    val propertyTitle: String = "",
    val propertyType: String = "",
    val price: Int = 0,
    @TypeConverters(Converters::class)
    val photoList: List<Photo> = emptyList(),
    val modificationDate: String = "",
    val propertyDescription: String = "",
    val propertyAddress: String = "",
    val propertyCityCode: String = "",
    val propertyCityName: String= "",
    val propertySurface: Int = 0,
    val propertyRooms: Int = 0,
    val propertyBedRooms: Int =0,
    val propertyBathRooms: Int = 0,
    val propertyPoiList: List<PointOfInterest> = emptyList(),
    val soldDate: String ="",
    val creationDate: String = "",
    val latitude:Double = 0.0,
    val longitude:Double = 0.0
)





