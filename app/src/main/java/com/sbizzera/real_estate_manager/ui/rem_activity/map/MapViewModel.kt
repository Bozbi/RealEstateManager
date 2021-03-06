package com.sbizzera.real_estate_manager.ui.rem_activity.map

import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.model.CustomMapMarkers
import com.sbizzera.real_estate_manager.data.repository.UserLocationRepo
import com.sbizzera.real_estate_manager.data.repository.PropertyRepository
import com.sbizzera.real_estate_manager.utils.architecture_components.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(
    private val application: Application,
    private val userLocationRepo: UserLocationRepo,
    private val markerUseCase: MarkerUseCase,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    propertyRepository: PropertyRepository
) : ViewModel() {

    val mapViewAction =
        SingleLiveEvent<MapViewAction>()
    private lateinit var mapMarkers: List<CustomMapMarkers>
    private lateinit var locationToFocus: Location
    private var mapIsReady = false
    private var markersReady = false
    private var locationIsReady = false

    init {
        viewModelScope.launch(IO) {
            val markers = markerUseCase.getMarkers()
            withContext(Main) {
                mapMarkers = markers
                markersReady = true
                shouldDisplay()
            }
        }
        viewModelScope.launch(IO) {
            val propertyId = currentPropertyIdRepository.currentPropertyIdLiveData.value
            if (propertyId == null) {
                val userLocation = userLocationRepo.getCurrentLocation()
                withContext(Main) {
                    locationToFocus = userLocation
                    locationIsReady = true
                    shouldDisplay()
                }
            } else {
                val property = propertyRepository.getPropertyById(propertyId)
                withContext(Main) {
                    locationToFocus = Location("")
                    locationToFocus.latitude = property.latitude
                    locationToFocus.longitude = property.longitude
                    locationIsReady = true
                    shouldDisplay()
                }
            }
        }
    }

    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun chooseAppropriateRequestPermission(shouldShowRequestPermissionRationale: Boolean) {
        if (shouldShowRequestPermissionRationale) {
            mapViewAction.value = MapViewAction.RequestRationalePermission
        } else {
            mapViewAction.value = MapViewAction.RequestNormalPermission
        }
    }


    fun checkLocationPermission() {
        if (!isLocationPermissionGranted()) {
            mapViewAction.value = MapViewAction.LocationPermissionToBeAsked
        } else {
            mapViewAction.value = MapViewAction.GetMap
        }
    }

    fun mapIsReady() {
        mapIsReady = true
        shouldDisplay()
    }

    private fun shouldDisplay() {
        if(mapIsReady&&markersReady&&locationIsReady){
            mapViewAction.value = MapViewAction.MapIsReady(mapMarkers,locationToFocus)
        }
    }

    fun mapIsNotReady() {
        mapIsReady = false
    }

    fun markerIsClicked(propertyId:String){
        currentPropertyIdRepository.currentPropertyIdLiveData.value = propertyId
    }

    sealed class MapViewAction {
        object LocationPermissionToBeAsked : MapViewAction()
        object GetMap : MapViewAction()
        object RequestNormalPermission : MapViewAction()
        object RequestRationalePermission : MapViewAction()
        class MapIsReady(val markers: List<CustomMapMarkers>, val locationToFocus : Location) : MapViewAction()
        object MapIsReadyWithoutLocation : MapViewAction()
    }

}