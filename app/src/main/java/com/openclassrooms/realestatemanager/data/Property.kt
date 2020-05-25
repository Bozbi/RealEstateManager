package com.openclassrooms.realestatemanager.data

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
@Entity(
    tableName = "properties"
)
data class Property (
    @ColumnInfo(name ="property_type") val propertyType : String,
    @ColumnInfo(name = "price") val price : Int,
    @ColumnInfo(name = "surface") val surface : Int,
    @ColumnInfo(name ="rooms_count") val roomsCount : Int,
    @ColumnInfo(name = "description") val description : String,
    @ColumnInfo(name = "address") val address : String,
    @ColumnInfo(name = "insert_date") val insertDate :LocalDateTime,
    @ColumnInfo(name = "sell_date") val sellDate :LocalDateTime?,
    @ColumnInfo(name="agent_name") val agentName : String

){
    @ColumnInfo(name ="property_id")
    @PrimaryKey(autoGenerate = true)
    val propertyId : Long = 0
}