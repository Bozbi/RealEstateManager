package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListPropertyFragmentViewModel (private val propertyRepository : PropertyRepository): ViewModel(){

    init {
        getAllProperties()
    }

    private val _uiModelLD = MutableLiveData<UiModel>()
    val uiModel :LiveData<UiModel> = _uiModelLD

    private fun getAllProperties (){
        viewModelScope.launch (IO) {
            val properties  = propertyRepository.getAllLocalProperties()
            withContext(Main){
                _uiModelLD.value =
                    UiModel(
                        properties
                    )
            }
        }
    }

}

