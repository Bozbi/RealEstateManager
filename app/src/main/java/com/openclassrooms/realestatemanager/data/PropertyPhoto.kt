package com.openclassrooms.realestatemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "property_photo",
    foreignKeys = [
        ForeignKey(
            entity = Property::class,
            parentColumns = ["property_id"],
            childColumns = ["property_id"]
        )
    ]
)
data class PropertyPhoto(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "main_photo") val mainPhoto : Boolean
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "photo_id")
    var photoId: Long = 0
}