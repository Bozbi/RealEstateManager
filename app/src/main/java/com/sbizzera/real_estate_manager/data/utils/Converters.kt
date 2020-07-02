package com.sbizzera.real_estate_manager.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.PointOfInterest

class Converters {

    @TypeConverter
    fun fromPhotoListToJson(photoList:List<Photo>):String{
        val gson = Gson()
        val type = object : TypeToken<List<Photo>>(){}.type
        return gson.toJson(photoList,type)
    }

    @TypeConverter
    fun fromJsonToPhotoList(photoListJson:String):List<Photo>{
        val gson = Gson()
        val type  = object : TypeToken<List<Photo>>(){}.type
        return gson.fromJson(photoListJson,type)
    }

    @TypeConverter
    fun fromPointOfInterestListToJson(poiList:List<PointOfInterest>):String{
        val gson = Gson()
        val type = object :TypeToken<List<PointOfInterest>>(){}.type
        return gson.toJson(poiList,type)
    }

    @TypeConverter
    fun fromJsonToPointOfInterestList(poiListJson:String):List<PointOfInterest>{
        val gson = Gson()
        val type  = object : TypeToken<List<PointOfInterest>>(){}.type
        return gson.fromJson(poiListJson,type)
    }
}