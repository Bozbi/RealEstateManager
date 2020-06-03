package com.openclassrooms.realestatemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey
    val propertyId: String,
    val propertyTitle: String,
    val propertyType: String,
    val price: Int

//    ,
//    @ColumnInfo(name = "surface") val surface: Int,
//    @ColumnInfo(name = "rooms_count") val roomsCount: Int,
//    @ColumnInfo(name = "description") val description: String,
//    @ColumnInfo(name = "address") val address: String,
//    @ColumnInfo(name = "insert_date") val insertDate: String,
//    @ColumnInfo(name = "sell_date") val sellDate: String?,
//    @ColumnInfo(name = "agent_name") val agentName: String

)