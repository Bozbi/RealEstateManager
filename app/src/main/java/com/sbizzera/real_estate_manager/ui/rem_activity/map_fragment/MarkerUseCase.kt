package com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment


import com.sbizzera.real_estate_manager.data.CustomMapMarkers
import com.sbizzera.real_estate_manager.data.property.PropertyRepository

class MarkerUseCase(private val propertyRepository: PropertyRepository) {

    suspend fun getMarkers(): List<CustomMapMarkers> {
        val allProperties = propertyRepository.getAllPropertiesAsync()
        val markers = mutableListOf<CustomMapMarkers>()
        allProperties.forEach {
            val marker = CustomMapMarkers(
                it.propertyId,
                it.latitude,
                it.longitude
            )
            markers.add(marker)
        }
        return markers
    }

}
