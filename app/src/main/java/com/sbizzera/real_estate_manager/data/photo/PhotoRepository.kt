package com.sbizzera.real_estate_manager.data.photo

import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.AppLocalDatabase

object PhotoRepository {
    private val photoDao = AppLocalDatabase.getInstance(App.getInstance()).photoDao()

    suspend fun getAllPhotosByPropertyId(propertyId:String)= photoDao.getAllPhotosByPropertyId(propertyId)
    suspend fun insertPhoto(photo: Photo) = photoDao.insertPhoto(photo)

}