package com.sbizzera.real_estate_manager.data.repository

import android.location.Location
import com.google.android.gms.location.LocationServices
import com.sbizzera.real_estate_manager.application.App
import kotlinx.coroutines.tasks.await

class UserLocationRepo private constructor() {
    companion object {
        fun newInstance() = UserLocationRepo()
    }

    suspend fun getCurrentLocation() : Location{
       return LocationServices.getFusedLocationProviderClient(App.instance).lastLocation.await()
    }


}