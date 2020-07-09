package com.sbizzera.real_estate_manager.data

import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.data.property.Property

object CurrentPropertyIdRepository {
    val currentPropertyIdLiveData = MutableLiveData<String>()
}