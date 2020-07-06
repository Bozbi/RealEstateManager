package com.sbizzera.real_estate_manager.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.data.property.Property

object CurrentPropertyRepository {
    private val currentPropertyLiveData = MutableLiveData<Property>()

    fun setCurrentProperty(property: Property) {
        currentPropertyLiveData.value = property
    }

    fun getCurrentProperty() = currentPropertyLiveData

}