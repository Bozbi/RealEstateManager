package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragmentViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListPropertyFragmentViewModel(
    private val propertyRepository: PropertyRepository,
    private val fileHelper : FileHelper
) : ViewModel() {

    private val _uiModelLD = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> = _uiModelLD

    val listPropertyViewAction = SingleLiveEvent<ListPropertyViewAction>()


    init {
        getAllProperties()
    }


    private fun getAllProperties() {
        viewModelScope.launch(IO) {
            val properties = propertyRepository.getAllLocalProperties()
            withContext(Main) {
                val uiProperties = fromPropertiesToUiProperties(properties)
                _uiModelLD.value =
                    UiModel(
                        uiProperties
                    )
            }
        }
    }

    fun addPropertyClicked() {
        listPropertyViewAction.value = AddPropertyClicked
    }

    fun refreshProperties() {
        getAllProperties()
    }

    private fun fromPropertiesToUiProperties(properties: List<Property>): List<PropertyUiModel> {
        val uiPropertyList = mutableListOf<PropertyUiModel>()
        properties.forEach {
            val uri  = fileHelper.getUriFromFileName(it.photoList[0].photoId,it.propertyId)
            val propertyToAdd = PropertyUiModel(
                title= it.propertyTitle,
                photoUri = uri,
                price = "$${it.price}",
                type = it.propertyType
            )
            uiPropertyList.add(propertyToAdd)
        }
        return uiPropertyList
    }

    sealed class ListPropertyViewAction {
        object AddPropertyClicked : ListPropertyViewAction()
    }
}

data class UiModel(val properties: List<PropertyUiModel>) {
}

data class PropertyUiModel(
    val title : String,
    val photoUri :String,
    val price : String,
    val type :String)

