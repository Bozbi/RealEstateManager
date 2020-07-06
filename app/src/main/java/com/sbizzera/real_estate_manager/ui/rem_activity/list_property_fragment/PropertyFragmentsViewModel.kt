package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import androidx.lifecycle.*
import com.sbizzera.real_estate_manager.data.CurrentPropertyRepository
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.PropertyFragmentsViewModel.DetailsViewAction.ModifyPropertyClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.PropertyFragmentsViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.utils.CUSTOM_DATE_FORMATTER
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate


class PropertyFragmentsViewModel(
    private val propertyRepository: PropertyRepository,
    private val currentPropertyRepository: CurrentPropertyRepository,
    private val fileHelper: FileHelper
) : ViewModel() {


    private val _ListUiStateLD = MutableLiveData<ListUiState>()
    val listUiState: LiveData<ListUiState> = _ListUiStateLD
    val listViewAction = SingleLiveEvent<ListPropertyViewAction>()

    val detailsUiStateLD: LiveData<DetailsUiState>
    val detailsViewAction = SingleLiveEvent<DetailsViewAction>()
    private lateinit var currentProperties: List<Property>


    init {
        getAllProperties()
        detailsUiStateLD = Transformations.map(currentPropertyRepository.getCurrentProperty()) {
            fromPropertyToDetailUiProperty(it)
        }
    }

    private fun getAllProperties() {
        viewModelScope.launch(IO) {
            currentProperties = propertyRepository.getAllLocalProperties()
            withContext(Main) {
                val listUiProperties = fromPropertiesToListUiProperties(currentProperties)
                _ListUiStateLD.value =
                    ListUiState(
                        listUiProperties
                    )
            }
        }
    }

    private fun fromPropertyToDetailUiProperty(property: Property): DetailsUiState {
        with(property) {
            return DetailsUiState(
                propertyTitle,
                "$${price}",
                "- $propertyType -",
                createAvailabilityText(creationDate, soldDate),
                fromPropertyPhotoListToDetailsPhotoList(photoList, propertyId),
                propertySurface.toString(),
                propertyRooms.toString(),
                propertyBedRooms.toString(),
                propertyBathRooms.toString(),
                propertyDescription,
                createAddressText(propertyAddress, propertyCityCode, propertyCityName),
                createPoiText(propertyPoiList),
                createStaticMapsUri(propertyAddress, propertyCityCode, propertyCityName)
            )
        }
    }

    private fun createStaticMapsUri(propertyAddress: String, propertyCityCode: String, propertyCityName: String) :String{
        var addressRequestString = "$propertyAddress $propertyCityCode $propertyCityName"
        addressRequestString = addressRequestString.replace("'","%").replace(" ","+")
        return "https://maps.googleapis.com/maps/api/staticmap?center=$addressRequestString&markers=color:blue%7C$addressRequestString&zoom=17&size=400x400&key=AIzaSyBShiG5Xmf-opNgKT0Ih02MGSV-uNTjTmw"
    }

    private fun createPoiText(propertyPoiList: List<PointOfInterest>): String {
        return if (propertyPoiList.isEmpty()) {
            "no known points of interest"
        } else {
            val poiListValues = mutableListOf<String>()
            propertyPoiList.forEach {
                poiListValues.add(it.value)
            }
            poiListValues.joinToString(", ", "Near ", ".")
        }
    }

    private fun createAvailabilityText(creationDate: String, soldDate: String): String {
        return if (soldDate.isEmpty()) {
            "available since ${creationDate}"
        } else {
            "sold on ${soldDate}"
        }
    }

    private fun fromPropertyPhotoListToDetailsPhotoList(
        photoList: List<Photo>,
        propertyId: String
    ): List<DetailsPhotoUiState> {
        val photoListToReturn = mutableListOf<DetailsPhotoUiState>()
        photoList.forEach {
            photoListToReturn.add(
                DetailsPhotoUiState(
                    it.title,
                    fileHelper.getUriFromFileName(it.photoId, propertyId)
                )
            )
        }
        return photoListToReturn
    }

    private fun createAddressText(propertyAddress: String, propertyCityCode: String, propertyCityName: String) =
        "$propertyAddress\n$propertyCityCode $propertyCityName"

    fun addPropertyClicked() {
        listViewAction.value = AddPropertyClicked
    }

    fun refreshProperties() {
        getAllProperties()
    }

    private fun fromPropertiesToListUiProperties(properties: List<Property>): List<ListPropertyItemUiState> {
        val uiPropertyList = mutableListOf<ListPropertyItemUiState>()
        properties.forEach {
            val uri = fileHelper.getUriFromFileName(it.photoList[0].photoId, it.propertyId)
            val propertyToAdd = ListPropertyItemUiState(
                title = it.propertyTitle,
                photoUri = uri,
                price = "$${it.price}",
                type = it.propertyType
            )
            uiPropertyList.add(propertyToAdd)
        }
        return uiPropertyList
    }

    fun modifyPropertyClicked() {
        detailsViewAction.value = ModifyPropertyClicked
    }

    fun onPropertyItemClick(position: Int) {
        currentPropertyRepository.setCurrentProperty(currentProperties[position])
        listViewAction.value = ListPropertyViewAction.DetailsPropertyClicked
    }

    sealed class ListPropertyViewAction {
        object AddPropertyClicked : ListPropertyViewAction()
        object DetailsPropertyClicked : ListPropertyViewAction()
    }

    sealed class DetailsViewAction() {
        object ModifyPropertyClicked : DetailsViewAction()
    }

}


data class ListUiState(
    val listPropertyItems: List<ListPropertyItemUiState>
)

data class ListPropertyItemUiState(
    val title: String,
    val photoUri: String,
    val price: String,
    val type: String
)

data class DetailsUiState(
    val title: String,
    val price: String,
    val type: String,
    val availableOrSoldSinceText: String,
    val listPropertyPhoto: List<DetailsPhotoUiState>,
    val surface: String,
    val roomsCount: String = "0",
    val bedRoomsCount: String = "0",
    val bathroomsCount: String = "0",
    val description: String,
    val addressText: String,
    val poiText: String = "no points of interest nearby",
    val staticMapUri: String
)

data class DetailsPhotoUiState(
    val photoTitle: String,
    val photoUri: String
)

