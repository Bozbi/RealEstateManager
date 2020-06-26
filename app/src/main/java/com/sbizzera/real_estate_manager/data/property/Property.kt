package com.sbizzera.real_estate_manager.data.property

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.utils.Converters


@Entity(tableName = "properties")
data class Property(
    @PrimaryKey
    val propertyId: String,
    val propertyTitle: String,
    val propertyType: String,
    val price: Int,
    @TypeConverters(Converters::class)
    val photoList:List<Photo>,
    val modificationDate : String,
    val propertyDescription :String,
    val propertyAddress : String,
    val propertyCityCode: String,
    val propertyCityName: String,
    val propertySurface :Int,
    val propertyRooms:Int,
    val propertyBedRooms:Int,
    val propertyBathRooms:Int,
    val propertyPoiList : List<PointOfInterest>,
    val soldDate : String

//    ,
//    @ColumnInfo(name = "surface") val surface: Int,
//    @ColumnInfo(name = "rooms_count") val roomsCount: Int,
//    @ColumnInfo(name = "description") val description: String,
//    @ColumnInfo(name = "address") val address: String,
//    @ColumnInfo(name = "insert_date") val insertDate: String,
//    @ColumnInfo(name = "sell_date") val sellDate: String?,
//    @ColumnInfo(name = "agent_name") val agentName: String

)