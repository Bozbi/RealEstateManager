package com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent

class MapViewModel(
    private val application: Application
) : ViewModel() {

    val mapViewAction = SingleLiveEvent<MapViewAction>()

    init {
        checkLocationPermission()
    }

    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun chooseAppropriateRequestPermission(shouldShowRequestPermissionRationale: Boolean) {
        if(shouldShowRequestPermissionRationale){
            mapViewAction.value = MapViewAction.RequestRationalePermission
        }else{
            mapViewAction.value = MapViewAction.RequestNormalPermission
        }
    }

    fun checkLocationPermission(){
        if(!isLocationPermissionGranted()){
            mapViewAction.value = MapViewAction.LocationPermissionToBeAsked
        }else{
            mapViewAction.value = MapViewAction.GetMap
        }
    }

    sealed class MapViewAction{
        object LocationPermissionToBeAsked: MapViewAction()
        object GetMap:MapViewAction()
        object RequestNormalPermission: MapViewAction()
        object RequestRationalePermission:MapViewAction()
    }

}