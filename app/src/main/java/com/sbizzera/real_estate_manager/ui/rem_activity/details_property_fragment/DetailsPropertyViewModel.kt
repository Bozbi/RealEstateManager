package com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.BuildConfig
import com.sbizzera.real_estate_manager.data.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyViewModel.DetailsViewAction.ModifyPropertyClicked
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent



class DetailsPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val fileHelper: FileHelper
) : ViewModel() {

    val detailsUiStateLD: LiveData<DetailsUiState>
    val detailsViewAction = SingleLiveEvent<DetailsViewAction>()


    init {
        detailsUiStateLD =
            Transformations.switchMap(currentPropertyIdRepository.currentPropertyIdLiveData) { currentPropertyId ->
                val propertyLiveData = propertyRepository.getPropertyById(currentPropertyId)
                Transformations.map(propertyLiveData) { property ->
                    fromPropertyToDetailUiState(property)
                }
            }

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  MODEL CONVERTERS
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun fromPropertyToDetailUiState(property: Property): DetailsUiState {
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


    private fun createAvailabilityText(creationDate: String, soldDate: String): String {
        return if (soldDate.isEmpty()) {
            "available since $creationDate"
        } else {
            "sold on $soldDate"
        }
    }


    private fun createAddressText(propertyAddress: String, propertyCityCode: String, propertyCityName: String) =
        "$propertyAddress\n$propertyCityCode $propertyCityName"

    private fun createPoiText(propertyPoiList: List<PointOfInterest>): String {
        return if (propertyPoiList.isEmpty()) {
            "no known points of interest"
        } else {
            val poiListValues = mutableListOf<String>()
            propertyPoiList.forEach {
                poiListValues.add(it.label)
            }
            poiListValues.joinToString(", ", "Near ", ".")
        }
    }

    private fun createStaticMapsUri(propertyAddress: String, propertyCityCode: String, propertyCityName: String): String {
        var addressRequestString = "$propertyAddress $propertyCityCode $propertyCityName"
        addressRequestString = addressRequestString.replace("'", "%").replace(" ", "+")
        return "https://maps.googleapis.com/maps/api/staticmap?center=$addressRequestString&markers=color:blue%7C$addressRequestString&zoom=19&size=600x600&key=${BuildConfig.MAPS_STATIC_API_KEY}"
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  DetailsFragment Methods
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun modifyPropertyClicked() {
        detailsViewAction.value = ModifyPropertyClicked
    }


    sealed class DetailsViewAction() {
        object ModifyPropertyClicked : DetailsViewAction()
    }
}


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






