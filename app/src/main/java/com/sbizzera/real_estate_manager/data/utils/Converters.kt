package com.sbizzera.real_estate_manager.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sbizzera.real_estate_manager.data.photo.Photo

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
}