package com.sbizzera.real_estate_manager.data.repository

import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property.EditUiState

class PropertyInModificationRepository private constructor(){

    companion object{
        val instance : PropertyInModificationRepository by lazy {
            PropertyInModificationRepository()
        }
    }
    val propertyInModificationLD = MutableLiveData<EditUiState>(null)
}