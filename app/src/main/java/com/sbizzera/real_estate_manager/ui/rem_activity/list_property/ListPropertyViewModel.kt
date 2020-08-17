package com.sbizzera.real_estate_manager.ui.rem_activity.list_property

import androidx.annotation.ColorRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.repository.FilterRepository
import com.sbizzera.real_estate_manager.data.repository.PropertyFilter
import com.sbizzera.real_estate_manager.data.repository.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel.FilterDialogViewAction.CreationDateRangeClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel.FilterDialogViewAction.SoldDateRangeClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel.ListPropertyViewAction.DetailsPropertyClicked
import com.sbizzera.real_estate_manager.utils.CUSTOM_DATE_FORMATTER
import com.sbizzera.real_estate_manager.utils.architecture_components.SingleLiveEvent
import com.sbizzera.real_estate_manager.utils.data_utils.PropertyComparator
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.collections.set


class ListPropertyViewModel(
    private val currentPropertyRepository: CurrentPropertyIdRepository,
    propertyRepository: PropertyRepository,
    private val fileHelper: FileHelper,
    private val filterRepository: FilterRepository
) : ViewModel() {

    val listUiStateLD = MediatorLiveData<ListUiState>()
    val listViewAction =
        SingleLiveEvent<ListPropertyViewAction>()
    val filterViewAction =
        SingleLiveEvent<FilterDialogViewAction>()
    val filterUiState: LiveData<FilterUiState>

    init {
        val allPropertiesLD = propertyRepository.getAllLocalProperties()
        val propertyFilterLD = filterRepository.filterLiveData
        val currentPropertyIdLD = currentPropertyRepository.currentPropertyIdLiveData

        listUiStateLD.addSource(filterRepository.filterLiveData) { propertyFilter ->
            combineSources(allPropertiesLD.value, propertyFilter, currentPropertyIdLD.value)
        }

        listUiStateLD.addSource(currentPropertyIdLD){currentPropertyRepositoryId->
            combineSources(allPropertiesLD.value,propertyFilterLD.value,currentPropertyRepositoryId)
        }

        listUiStateLD.addSource(allPropertiesLD) { allProperties ->
            combineSources(allProperties, propertyFilterLD.value,currentPropertyIdLD.value)
        }

        filterUiState = Transformations.map(filterRepository.filterLiveData) { filters ->
            fromPropertyFilterToFilterUiState(filters)
        }
    }

    private fun combineSources(allProperties: List<Property>?, propertyFilter: PropertyFilter?, currentPropertyId: String?) {
        if (allProperties == null || propertyFilter == null) {
            return
        }
        val listOfFilteredProperties = mutableListOf<Property>()

        Collections.sort(allProperties,
            PropertyComparator()
        )

        allProperties.forEach { property ->
            val doesPropertyMatchFilters = doesPropertyMatchFilters(property, propertyFilter)

            if (doesPropertyMatchFilters) {
                listOfFilteredProperties.add(property)
            }
        }

        val listUiStateToReturn =
            ListUiState(
                fromPropertiesToListUiProperties(listOfFilteredProperties,currentPropertyId)
            )
        listUiStateLD.value = listUiStateToReturn
    }

    private fun doesPropertyMatchFilters(property: Property, propertyFilter: PropertyFilter): Boolean {
        if (
            !availabilityDateMatchesFilter(property.creationDate, propertyFilter.createDateRange) ||
            !soldDateMatchesFilters(property.soldDate, propertyFilter.soldDateRange) ||
            !priceMatchesFilters(property.price, propertyFilter.priceRange) ||
            !surfaceMatchesFilters(property.propertySurface, propertyFilter.surfaceRange) ||
            !roomCountMatchesFilters(property.propertyRooms, propertyFilter.roomRange) ||
            !poiMapMatchesFilters(property.propertyPoiList, propertyFilter.poiMap)
        ) {
            return false
        }
        return true
    }

    private fun poiMapMatchesFilters(
        propertyPoiList: List<PointOfInterest>,
        poiMap: Map<PointOfInterest, Boolean>
    ): Boolean {
        poiMap.filter { poiInMapFilter ->
            poiInMapFilter.value
        }.forEach { poiWantedInFilter ->
            if (!propertyPoiList.contains(poiWantedInFilter.key)) {
                return false
            }
        }
        return true
    }

    private fun roomCountMatchesFilters(propertyRooms: Int, roomRange: IntRange?) =
        roomRange == null || propertyRooms in roomRange || (propertyRooms > 10 && roomRange.last == 10)


    private fun surfaceMatchesFilters(propertySurface: Int, surfaceRange: IntRange?) =
        surfaceRange == null || propertySurface in surfaceRange || (propertySurface > 200 && surfaceRange.last == 200)

    private fun priceMatchesFilters(price: Int, priceRange: IntRange?) =
        priceRange == null || price in priceRange || (price > 3000000 && priceRange.last == 3000000)

    private fun soldDateMatchesFilters(soldDate: String, soldDateRange: LongRange?): Boolean {
        if (soldDateRange == null) {
            return true
        }
        if (soldDate.isEmpty()) {
            return false
        }


        val soldDateLocalDate = LocalDate.parse(soldDate, CUSTOM_DATE_FORMATTER)
        val rangeBeginLocalDate =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(soldDateRange.first), ZoneOffset.UTC).toLocalDate()
        val rangeEndLocalDate =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(soldDateRange.last), ZoneOffset.UTC).toLocalDate()
        if (soldDateLocalDate.isEqual(rangeBeginLocalDate) ||
            soldDateLocalDate.isEqual(rangeEndLocalDate) ||
            (soldDateLocalDate.isAfter(rangeBeginLocalDate) && soldDateLocalDate.isBefore(rangeEndLocalDate))
        ){
            return true
        }
        return false
    }

    private fun availabilityDateMatchesFilter(creationDate: String, creationDateRange: LongRange?): Boolean {
        if (creationDateRange == null) {
            return true
        }
        val creationDateLocalDate = LocalDate.parse(creationDate, DateTimeFormatter.ISO_DATE_TIME)
        val rangeBeginLocalDate =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(creationDateRange.first), ZoneOffset.UTC).toLocalDate()
        val rangeEndLocalDate =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(creationDateRange.last), ZoneOffset.UTC).toLocalDate()
        if (creationDateLocalDate.isEqual(rangeBeginLocalDate) ||
            creationDateLocalDate.isEqual(rangeEndLocalDate) ||
            (creationDateLocalDate.isAfter(rangeBeginLocalDate) && creationDateLocalDate.isBefore(rangeEndLocalDate))
        ){
            return true
        }
        return false
    }

    private fun fromPropertyFilterToFilterUiState(filters: PropertyFilter): FilterUiState {
        val priceMin = if (filters.priceRange == null) 0 else filters.priceRange.first
        val priceMax = if (filters.priceRange == null) 3000000 else filters.priceRange.last
        val surfaceMin = if (filters.surfaceRange == null) 0 else filters.surfaceRange.first
        val surfaceMax = if (filters.surfaceRange == null) 200 else filters.surfaceRange.last
        val roomMin = if (filters.roomRange == null) 0 else filters.roomRange.first
        val roomMax = if (filters.roomRange == null) 10 else filters.roomRange.last
        val poiMap = filters.poiMap
        val creationDateRangeText = createDateRangeText(filters.createDateRange)
        val soldDateRangeText = soldDateRangeText(filters.soldDateRange)

        return FilterUiState(
            priceMin.toFloat(),
            priceMax.toFloat(),
            surfaceMin.toFloat(),
            surfaceMax.toFloat(),
            roomMin.toFloat(),
            roomMax.toFloat(),
            poiMap,
            creationDateRangeText,
            soldDateRangeText
        )
    }

    private fun soldDateRangeText(soldDateRange: LongRange?): String {
        if (soldDateRange == null) {
            return ""
        }
        val rangeBeginString =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(soldDateRange.first), ZoneOffset.UTC).format(CUSTOM_DATE_FORMATTER)
        val rangeEndString =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(soldDateRange.last), ZoneOffset.UTC).format(CUSTOM_DATE_FORMATTER)
        return "$rangeBeginString and $rangeEndString"
    }

    private fun createDateRangeText(createDateRange: LongRange?): String {
        if (createDateRange == null) {
            return ""
        }
        val rangeBeginString = LocalDateTime.ofInstant(Instant.ofEpochMilli(createDateRange.first), ZoneOffset.UTC)
            .format(CUSTOM_DATE_FORMATTER)
        val rangeEndString =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(createDateRange.last), ZoneOffset.UTC).format(CUSTOM_DATE_FORMATTER)
        return "$rangeBeginString and $rangeEndString"
    }

    fun addPropertyClicked() {
        currentPropertyRepository.currentPropertyIdLiveData.value = ""
        listViewAction.value = AddPropertyClicked
    }

    fun onPropertyItemClick(propertyId: String) {
        currentPropertyRepository.currentPropertyIdLiveData.value = propertyId
        listViewAction.value = DetailsPropertyClicked
    }

    private fun fromPropertiesToListUiProperties(properties: List<Property>, currentPropertyId: String?): List<ListPropertyItemUiState> {
        val uiPropertyList = mutableListOf<ListPropertyItemUiState>()
        properties.forEach {
            val uri = fileHelper.getUriFromFileName(it.photoList[0].photoId, it.propertyId)
            val colorRes = getBackgroundColor(it.propertyId,currentPropertyId)
            val propertyToAdd = ListPropertyItemUiState(
                id = it.propertyId,
                title = it.propertyTitle,
                photoUri = uri,
                price = "$${it.price}",
                type = it.propertyType,
                backGroundColor = colorRes
            )
            uiPropertyList.add(propertyToAdd)
        }
        return uiPropertyList
    }

    private fun getBackgroundColor(propertyId: String,currentPropertyId: String?): Int {
        return if(currentPropertyId==null || propertyId != currentPropertyId){
            android.R.color.white
        }else{
            R.color.colorAccent
        }
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
            roomRange = IntRange(minRoom.toInt(), maxRoom.toInt())
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
        filterViewAction.value = CreationDateRangeClicked
    }

    fun onSoldSinceDateClick() {
        filterViewAction.value = SoldDateRangeClicked
    }

    fun onCreationDateRangeChange(beginRange: Long, endRange: Long) {
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value!!.copy(
            createDateRange = LongRange(beginRange, endRange)
        )
    }

    fun onSoldDateChange(beginRange: Long, endRange: Long) {
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value!!.copy(
            soldDateRange = LongRange(beginRange, endRange)
        )
    }

    fun onAvailableDateCleared() {
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value!!.copy(
            createDateRange = null
        )
    }

    fun onSoldSinceDateCleared() {
        filterRepository.filterLiveData.value = filterRepository.filterLiveData.value!!.copy(
            soldDateRange = null
        )
    }

    fun resetFilters() {
        filterRepository.filterLiveData.value =
            PropertyFilter()
    }


    sealed class ListPropertyViewAction {
        object AddPropertyClicked : ListPropertyViewAction()
        object DetailsPropertyClicked : ListPropertyViewAction()
    }

    sealed class FilterDialogViewAction {
        object SoldDateRangeClicked : FilterDialogViewAction()
        object CreationDateRangeClicked : FilterDialogViewAction()
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
    val type: String,
    @ColorRes
    val backGroundColor : Int
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
    val soldAfter: String
)
