package com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class MapViewModel(
    private val application: Application
) : ViewModel() {

    val mapViewAction = SingleLiveEvent<MapViewAction>()

    init {
        if(!isLocationPermissionGranted()){
            mapViewAction.value = MapViewAction.LocationPermissionToBeAsked
        }
    }

    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    sealed class MapViewAction{
        object LocationPermissionToBeAsked: MapViewAction()
    }

}