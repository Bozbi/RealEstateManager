package com.sbizzera.real_estate_manager.ui.rem_activity.details_property

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sbizzera.real_estate_manager.BuildConfig
import com.sbizzera.real_estate_manager.data.repository.CurrentPhotoPositionRepo
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.model.Photo
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.data.repository.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property.DetailsPropertyViewModel.DetailsViewAction.ModifyPropertyClicked
import com.sbizzera.real_estate_manager.utils.CUSTOM_DATE_FORMATTER
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import com.sbizzera.real_estate_manager.utils.architecture_components.SingleLiveEvent
import org.threeten.bp.format.DateTimeFormatter


class DetailsPropertyViewModel(
    propertyRepository: PropertyRepository,
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val currentPhotoPositionRepo: CurrentPhotoPositionRepo,
    private val fileHelper: FileHelper

) : ViewModel() {

    var detailsUiStateLD: LiveData<DetailsUiState>
    val detailsViewAction =
        SingleLiveEvent<DetailsViewAction>()


    init {
        val currentPropertyId = currentPropertyIdRepository.currentPropertyIdLiveData.value
        detailsUiStateLD = MutableLiveData(null)
         if (currentPropertyId != null) {
            val propertyLiveData: LiveData<Property> = propertyRepository.getPropertyByIdLD(currentPropertyId)
            detailsUiStateLD = Transformations.map(propertyLiveData) { property ->
                fromPropertyToDetailUiState(property)
            }
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  MODEL CONVERTERS
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun fromPropertyToDetailUiState(property: Property?): DetailsUiState {
        if (property != null) {
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
                    createStaticMapsUri(propertyAddress, propertyCityCode, propertyCityName),
                    createAgentText(estateAgent)
                )
            }
        } else {
            return DetailsUiState()
        }
    }

    private fun createAgentText(estateAgent: String?): String {
        return "added by $estateAgent"
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
            val date = org.threeten.bp.LocalDateTime.parse(creationDate, DateTimeFormatter.ISO_DATE_TIME)
            val stringDate = date.format(CUSTOM_DATE_FORMATTER)
            "available since $stringDate"
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
        return "https://maps.googleapis.com/maps/api/staticmap?center=$addressRequestString&markers=color:blue%7C$addressRequestString&zoom=19&size=640x64" +
                "0&key=${BuildConfig.MAPS_STATIC_API_KEY}"
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  DetailsFragment Methods
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun modifyPropertyClicked() {
        detailsViewAction.value = ModifyPropertyClicked
    }

    fun photoClicked(position: Int) {
        currentPhotoPositionRepo.currentPhotoPosition = position
    }

    fun onViewHolderBound(position: Int) {
        if (position == currentPhotoPositionRepo.currentPhotoPosition) {
            detailsViewAction.value = DetailsViewAction.ViewHolderReady
        }
    }

    fun getCurrentPhotoPosition(): Int = currentPhotoPositionRepo.currentPhotoPosition

    fun checkScrollNecessity(firstCompletelyVisibleItemPosition: Int, lastCompletelyVisibleItemPosition: Int) {
        if (currentPhotoPositionRepo.currentPhotoPosition !in firstCompletelyVisibleItemPosition..lastCompletelyVisibleItemPosition) {
            detailsViewAction.value = DetailsViewAction.ScrollToPosition(currentPhotoPositionRepo.currentPhotoPosition)
        }
    }

    override fun onCleared() {
        currentPhotoPositionRepo.currentPhotoPosition = -1
    }


    sealed class DetailsViewAction {
        object ModifyPropertyClicked : DetailsViewAction()
        object ViewHolderReady : DetailsViewAction()
        class ScrollToPosition(val position: Int) : DetailsViewAction()
    }
}


data class DetailsUiState(
    val title: String = "",
    val price: String = "",
    val type: String = "",
    val availableOrSoldSinceText: String = "",
    val listPropertyPhoto: List<DetailsPhotoUiState> = listOf(),
    val surface: String = "",
    val roomsCount: String = "0",
    val bedRoomsCount: String = "0",
    val bathroomsCount: String = "0",
    val description: String = "",
    val addressText: String = "",
    val poiText: String = "no points of interest nearby",
    val staticMapUri: String = "",
    val agentText: String = ""
)

data class DetailsPhotoUiState(
    val photoTitle: String,
    val photoUri: String
)






