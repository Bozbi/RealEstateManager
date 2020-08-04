package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.data.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyViewModel.ListPropertyViewAction.DetailsPropertyClicked
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import org.threeten.bp.LocalDate
import org.threeten.bp.Year


class ListPropertyViewModel(
    private val currentPropertyRepository: CurrentPropertyIdRepository,
    propertyRepository: PropertyRepository,
    private val fileHelper: FileHelper,
    private val filterRepository: FilterRepository
) : ViewModel() {

    val listUiState: LiveData<ListUiState>
    val listViewAction = SingleLiveEvent<ListPropertyViewAction>()
    val filterViewAction = SingleLiveEvent<FilterDialogViewAction>()
    val filterUiState: LiveData<FilterUiState>

    init {
        listUiState = Transformations.map(propertyRepository.getAllLocalProperties()) {
            ListUiState(fromPropertiesToListUiProperties(it))
        }
        filterUiState = Transformations.map(filterRepository.filterLiveData) { filters ->
            fromPropertyFilterToFilterUiState(filters)
        }
    }

    private fun fromPropertyFilterToFilterUiState(filters: PropertyFilter): FilterUiState {
        val priceMin = if (filters.priceRange == null) 0 else filters.priceRange.first
        val priceMax = if (filters.priceRange == null) 3000000 else filters.priceRange.last
        val surfaceMin = if (filters.surfaceRange == null) 0 else filters.surfaceRange.first
        val surfaceMax = if (filters.surfaceRange == null) 200 else filters.surfaceRange.last
        val roomMin = if (filters.roomRange == null) 0 else filters.roomRange.first
        val roomMax = if (filters.roomRange == null) 10 else filters.roomRange.last
        val poiMap = filters.poiMap

        return FilterUiState(
            priceMin.toFloat(),
            priceMax.toFloat(),
            surfaceMin.toFloat(),
            surfaceMax.toFloat(),
            roomMin.toFloat(),
            roomMax.toFloat(),
            poiMap,
            filters.availableSince ?: "all properties",
            if (filters.availableSince == null) View.INVISIBLE else View.VISIBLE,
            filters.soldSince ?: "all properties",
            if (filters.soldSince == null) View.INVISIBLE else View.VISIBLE
        )
    }


    private fun createPoiValuesMap(poiList: List<PointOfInterest>): Map<String, Boolean> {
        val map = mutableMapOf<String, Boolean>()
        PointOfInterest.values().forEach {
            map[it.label] = poiList.contains(PointOfInterest.valueOf(it.name))
        }
        return map
    }

    fun addPropertyClicked() {
        currentPropertyRepository.currentPropertyIdLiveData.value = null
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

    fun onPriceRangeFilterChange(minPrice: Float, maxPrice: Float) {
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value?.copy(
            priceRange = IntRange(minPrice.toInt(), maxPrice.toInt())
        )
    }

    fun onSurfaceRangeFilterChange(minSurface: Float, maxSurface: Float) {
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value?.copy(
            surfaceRange = IntRange(minSurface.toInt(), maxSurface.toInt())
        )
    }

    fun onRoomRangeFilterChange(minRoom: Float, maxRoom: Float) {
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value?.copy(
            surfaceRange = IntRange(minRoom.toInt(), maxRoom.toInt())
        )
    }

    fun onChipCheckedChange(chipName: String, isChecked: Boolean) {
        val poiMap = filterRepository.filterLiveData.value!!.poiMap.toMutableMap()
        poiMap[PointOfInterest.valueOf(chipName)] = isChecked
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value!!.copy(
            poiMap = poiMap
        )
    }

    fun onAvailableSinceDateClick() {

    }

    fun onSoldSinceDateClick() {
        val date = LocalDate.now()
        filterViewAction.value= FilterDialogViewAction.SoldDateClicked(date.year,date.monthValue,date.dayOfMonth)
    }


    sealed class ListPropertyViewAction {
        object AddPropertyClicked : ListPropertyViewAction()
        object DetailsPropertyClicked : ListPropertyViewAction()
    }

    sealed class FilterDialogViewAction{
        class SoldDateClicked(val year: Int, val month : Int , val day : Int, val tag: String = "soldDate") : FilterDialogViewAction()
        class AvailableDateClicked(val year: Int, val month : Int , val day : Int, val tag: String = "availableDate") : FilterDialogViewAction()
    }
}

data class ListUiState(
    val listPropertyItems: List<ListPropertyItemUiState>
)

data class ListPropertyItemUiState(
    val id: String,
    val title: String,
    val photoUri: String,
    val price: String,
    val type: String
)

data class FilterUiState(
    val priceMin: Float,
    val priceMax: Float,
    val surfaceMin: Float,
    val surfaceMax: Float,
    val roomMin: Float,
    val roomMax: Float,
    val poiMap: Map<PointOfInterest, Boolean>,
    val availableAfter: String,
    val clearAvailableSinceImageVisibility: Int = View.INVISIBLE,
    val soldAfter: String,
    val clearSoldSinceImageVisibility: Int = View.INVISIBLE
)