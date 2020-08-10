package com.sbizzera.real_estate_manager.utils

import android.location.Geocoder
import com.sbizzera.real_estate_manager.App

class GeocodeResolver {
    fun getLatitude(address: String):Double{
        val geocode = Geocoder(App.instance).getFromLocationName(address, 1)[0]
        return geocode.latitude
    }
    fun getLongitude(address:String):Double{
        val geocode = Geocoder(App.instance).getFromLocationName(address, 1)[0]
        return geocode.longitude
    }

    sealed class LatOrLng{
        object LATITUDE : LatOrLng()
        object LONGITUDE : LatOrLng()
    }
}