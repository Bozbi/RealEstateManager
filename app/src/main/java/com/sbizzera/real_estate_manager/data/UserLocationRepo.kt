package com.sbizzera.real_estate_manager.data

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sbizzera.real_estate_manager.App
import kotlinx.coroutines.tasks.await

class UserLocationRepo private constructor() {
    companion object {
        fun newInstance() = UserLocationRepo()
    }

    suspend fun getCurrentLocation() : Location{
       return LocationServices.getFusedLocationProviderClient(App.instance).lastLocation.await()
    }


}