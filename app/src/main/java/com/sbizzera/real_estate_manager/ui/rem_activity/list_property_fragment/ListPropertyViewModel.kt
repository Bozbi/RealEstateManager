package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.data.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyViewModel.ListPropertyViewAction.DetailsPropertyClicked
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent


class ListPropertyViewModel(
    private val currentPropertyRepository: CurrentPropertyIdRepository,
    private val propertyRepository: PropertyRepository,
    private val fileHelper : FileHelper
) : ViewModel() {

    val listUiState: LiveData<ListUiState>
    val listViewAction = SingleLiveEvent<ListPropertyViewAction>()

    init {
        listUiState = Transformations.map(propertyRepository.getAllLocalProperties()) {
            ListUiState(fromPropertiesToListUiProperties(it))
        }
    }


    fun addPropertyClicked() {
        currentPropertyRepository.currentPropertyIdLiveData.value = ""
        listViewAction.value = AddPropertyClicked
    }

    fun onPropertyItemClick(propertyId: String) {
        currentPropertyRepository.currentPropertyIdLiveData.value = propertyId
        listViewAction.value = DetailsPropertyClicked
    }

    private fun fromPropertiesToListUiProperties(properties: List<Property>): List<ListPropertyItemUiState> {
        val uiPropertyList = mutableListOf<ListPropertyItemUiState>()
        properties.forEach {
            val uri = fileHelper.getUriFromFileName(it.photoList[0].photoId, it.propertyId)
            val propertyToAdd = ListPropertyItemUiState(
                id = it.propertyId,
                title = it.propertyTitle,
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
        object DetailsPropertyClicked : ListPropertyViewAction()
    }
}

data class ListUiState(
    val listPropertyItems: List<ListPropertyItemUiState>
)

data class ListPropertyItemUiState(
    val id : String,
    val title: String,
    val photoUri: String,
    val price: String,
    val type: String
)