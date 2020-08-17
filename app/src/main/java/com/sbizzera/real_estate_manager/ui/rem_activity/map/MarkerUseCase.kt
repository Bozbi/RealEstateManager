package com.sbizzera.real_estate_manager.ui.rem_activity.map


import com.sbizzera.real_estate_manager.data.model.CustomMapMarkers
import com.sbizzera.real_estate_manager.data.repository.PropertyRepository

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
