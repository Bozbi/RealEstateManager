package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.model.Photo
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.data.repository.*
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property.EditPropertyViewModel.EditPropertyViewAction.LaunchEditor
import com.sbizzera.real_estate_manager.utils.*
import com.sbizzera.real_estate_manager.utils.architecture_components.SingleLiveEvent
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import com.sbizzera.real_estate_manager.utils.helper.GeocodeResolver
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.Clock
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.collections.set

class EditPropertyViewModel(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val propertyInModificationRepository: PropertyInModificationRepository,
    private val propertyRepository: PropertyRepository,
    private val fileHelper: FileHelper,
    private val currentPhotoRepository: CurrentEditedPhotoRepository,
    private val context: Context,
    private val geocodeResolver: GeocodeResolver,
    private val sharedPreferencesRepo: SharedPreferencesRepo,
    private val clock : Clock,
    private val coroutineProvider: CoroutineContextProvider
) : ViewModel() {

    val editViewAction =
        SingleLiveEvent<EditPropertyViewAction?>()
    val editEvent =
        SingleLiveEvent<EditPropertyEvent>()
    val editUiStateLD: LiveData<EditUiState>
    private lateinit var initialProperty: Property
    private lateinit var tempPhotoUri: String


    init {
        editUiStateLD =
            Transformations.switchMap(currentPropertyIdRepository.currentPropertyIdLiveData) { propertyId ->
                if (propertyId.isNotEmpty()) {
                    Transformations.switchMap(propertyRepository.getPropertyByIdLD(propertyId)) { property ->
                        initialProperty = property
                        propertyInModificationRepository.propertyInModificationLD.value =
                            fromPropertyToEditUiState(property, fileHelper)
                        propertyInModificationRepository.propertyInModificationLD
                    }
                } else {
                    initialProperty = Property()
                    propertyInModificationRepository.propertyInModificationLD.value = EditUiState()
                    propertyInModificationRepository.propertyInModificationLD
                }
            }
    }


    fun takePhotoFromCameraClicked() {
        tempPhotoUri = fileHelper.createEmptyTempPhotoFileAndGetUriBack()
        editViewAction.value = EditPropertyViewAction.TakePhotoFromCamera(tempPhotoUri)
    }

    fun takePhotoFromGalleryClicked() {
        editViewAction.value = EditPropertyViewAction.TakePhotoFromGallery
    }

    fun soldDatePickerClicked(year: Int,month: Int,dayOfMonth: Int) {
        editViewAction.value =
            EditPropertyViewAction.DisplayDatePicker(year, month, dayOfMonth)
    }

    fun savePropertyClicked() {
        if (!allInfoCorrect()) {
            return
        }
        if (propertyHasNotChanged(LocalDateTime.now(clock))) {
            editViewAction.value = EditPropertyViewAction.CloseFragment
            return
        }
        saveProperty(LocalDateTime.now(clock))
    }

    private fun propertyHasNotChanged(dateTimeNow: LocalDateTime): Boolean {
        val propertyModified = fromEditUiStateToProperty(dateTimeNow)
        return propertyModified.hasNotChanged(initialProperty)
    }

    private fun allInfoCorrect(): Boolean {
        var allInfoCorrect = true
        var photoCountCorrect = true
        var state = propertyInModificationRepository.propertyInModificationLD.value!!

        state = state.copy(
            propertyTitleError = null,
            addPhotoVisibility = View.INVISIBLE,
            propertyDescriptionError = null,
            propertyAddressError = null,
            propertyCityCodeError = null,
            propertyCityNameError = null,
            propertyPriceError = null,
            propertyTypeError = null,
            propertySurfaceError = null
        )
        with(state) {
            if (photoList.isEmpty()) {
                photoCountCorrect = false
                state = state.copy(addPhotoVisibility = View.VISIBLE)
                editViewAction.value = EditPropertyViewAction.NoPhotoError
            }
            if (propertyTitle.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertyTitleError = context.getString(R.string.property_title_error))
            }
            if (propertyDescription.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertyDescriptionError = context.getString(R.string.property_description_error))
            }

            if (this.propertyAddress.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertyAddressError = context.getString(R.string.property_address_error))
            }
            if (this.propertyCityCode.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertyCityCodeError = context.getString(R.string.property_city_code_error))
            }
            if (propertyCityName.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertyCityNameError = context.getString(R.string.property_city_name_error))
            }
            if (propertyPrice.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertyPriceError = context.getString(R.string.property_price_error))
            }
            if (propertyType.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertyTypeError = context.getString(R.string.property_type_error))
            }
            if (propertySurface.isNullOrEmpty()) {
                allInfoCorrect = false
                state = state.copy(propertySurfaceError = context.getString(R.string.property_surface_error))
            }
        }

        propertyInModificationRepository.propertyInModificationLD.value = state
        if (!photoCountCorrect) {
            editViewAction.value = EditPropertyViewAction.NoPhotoError
        } else {
            if (!allInfoCorrect) {
                editViewAction.value = EditPropertyViewAction.FillInError
            }
        }

        return allInfoCorrect && photoCountCorrect
    }


    private fun saveProperty(dateTimeNow: LocalDateTime) {
        val currentEditUiState = propertyInModificationRepository.propertyInModificationLD.value!!
        checkInsertOrDeletePhoto(currentEditUiState)
        val propertyToInsert = fromEditUiStateToProperty(dateTimeNow)
        viewModelScope.launch(coroutineProvider.io) {
            val inserted = propertyRepository.insertLocalProperty(propertyToInsert)
            withContext(coroutineProvider.main) {
                editViewAction.value = EditPropertyViewAction.CloseFragment
                if (inserted.toInt() != -1) {
                    editEvent.value = EditPropertyEvent.PropertySaved
                }
            }
        }
    }

    private fun checkInsertOrDeletePhoto(property: EditUiState) {
        property.photoList.forEach {
            if (!fileHelper.fileExistsInPropertyFolder(it.photoId, property.propertyId)) {
                fileHelper.saveImageToPropertyFolder(it.photoUri, property.propertyId, it.photoId)
            }
        }
        val photoList = mutableListOf<Photo>()
        property.photoList.forEach {
            photoList.add(
                Photo(it.photoId, it.photoTitle)
            )
        }
        fileHelper.deleteOldPhotosFromPropertyDirectory(property.propertyId, photoList.toList())
        fileHelper.deleteCache()
    }


    fun editPhotoClicked(recyclerPosition: Int) {
        currentPhotoRepository.currentPhotoLD.value =
            Pair(
                propertyInModificationRepository.propertyInModificationLD.value!!.photoList[recyclerPosition],
                recyclerPosition
            )
        editViewAction.value = LaunchEditor
    }

    @SuppressLint("DefaultLocale")
    private fun fromEditUiStateToProperty(dateTimeNow :LocalDateTime): Property {
        val currentPropertyToInsert = propertyInModificationRepository.propertyInModificationLD.value!!
        val photoList = createPhotoList(currentPropertyToInsert.photoList)
        with(currentPropertyToInsert) {
            return Property(
                propertyId,
                propertyTitle.toString().capitalize(),
                propertyType.toString(),
                propertyPrice.toString().toIntOrNull() ?: 0,
                photoList,
                dateTimeNow.toString(),
                propertyDescription.toString(),
                propertyAddress.toString(),
                propertyCityCode.toString(),
                propertyCityName.toString(),
                propertySurface?.toString()?.toIntOrNull() ?: 0,
                propertyRoomCount?.toString()?.toIntOrNull() ?: 0,
                propertyBedroomCount?.toString()?.toIntOrNull() ?: 0,
                propertyBathroomCount?.toString()?.toIntOrNull() ?: 0,
                this@EditPropertyViewModel.createPoiList(propertyPoiMap),
                propertySoldDate?.toString() ?: "",
                propertyCreationDate ?: dateTimeNow.format(DateTimeFormatter.ISO_DATE_TIME),
                propertyLat,
                propertyLng,
                sharedPreferencesRepo.getUserName()
            )
        }
    }

    private fun getLatitudeOrLongitude(currentPropertyToInsert: EditUiState, latOrLng: GeocodeResolver.LatOrLng): Double {
        val address =
            "${currentPropertyToInsert.propertyAddress} ${currentPropertyToInsert.propertyCityCode} ${currentPropertyToInsert.propertyCityName}"
        return when (latOrLng) {
            GeocodeResolver.LatOrLng.LATITUDE -> {
                try {
                    geocodeResolver.getLatitude(address)
                } catch (e: Exception) {
                    0.0
                }
            }
            GeocodeResolver.LatOrLng.LONGITUDE -> {
                try {
                    geocodeResolver.getLongitude(address)
                } catch (e: Exception) {
                    0.0
                }
            }
        }
    }

    private fun fromPropertyToEditUiState(property: Property?, fileHelper: FileHelper): EditUiState {
        if (property == null) {
            return EditUiState()
        }
        with(property) {
            return EditUiState(
                propertyId = propertyId,
                photoList = photoList.toPhotoInEditUiState(fileHelper, propertyId),
                addPhotoVisibility = if (this.photoList.isEmpty()) View.VISIBLE else View.INVISIBLE,
                propertyTitle = propertyTitle,
                propertyDescription = propertyDescription,
                propertyAddress = propertyAddress,
                propertyCityCode = propertyCityCode,
                propertyCityName = propertyCityName,
                propertyPrice = price.toString(),
                propertyType = propertyType,
                propertySurface = propertySurface.toString(),
                propertyRoomCount = propertyRooms.toString(),
                propertyBedroomCount = propertyBedRooms.toString(),
                propertyBathroomCount = propertyBathRooms.toString(),
                propertySoldDate = soldDate,
                propertyPoiMap = createPoiMap(propertyPoiList),
                propertyAgent = estateAgent,
                propertyCreationDate = creationDate,
                propertyLat = latitude,
                propertyLng = longitude
            )
        }
    }

    private fun createPoiMap(
        poiList: List<PointOfInterest>
    ): MutableMap<PointOfInterest, Boolean> {
        val propertyPoiMap = mutableMapOf<PointOfInterest, Boolean>()
        poiList.forEach {
            propertyPoiMap[it] = true
        }
        return propertyPoiMap
    }

    private fun createPoiList(
        poiMap: Map<PointOfInterest, Boolean>
    ): List<PointOfInterest> {
        val poiListToReturn = mutableListOf<PointOfInterest>()
        poiMap.forEach { entry ->
            if (entry.value) {
                poiListToReturn.add(entry.key)
            }
        }

        return poiListToReturn
    }

    private fun createPhotoList(photoUiModelList: List<PhotoOnEdit>): List<Photo> {
        val listToReturn: MutableList<Photo> = mutableListOf()
        photoUiModelList.forEach {
            listToReturn.add(Photo(it.photoId, it.photoTitle))
        }
        return listToReturn
    }

    fun onTitleChange(title: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyTitle = title)
    }

    fun onDescriptionChange(description: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyDescription = description)
    }

    fun onAddressChange(address: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyAddress = address)
        fetchNewLatLng()
    }

    fun onCityCodeChange(cityCode: String) {
        fetchNewLatLng()
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyCityCode = cityCode)
    }

    fun onCityNameChange(cityName: String) {
        fetchNewLatLng()
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyCityName = cityName)
    }

    private fun fetchNewLatLng() {
        val currentPropertyToInsert = propertyInModificationRepository.propertyInModificationLD.value!!
        viewModelScope.launch(coroutineProvider.io) {
            val newLat = getLatitudeOrLongitude(currentPropertyToInsert, GeocodeResolver.LatOrLng.LATITUDE)
            val newLng = getLatitudeOrLongitude(currentPropertyToInsert, GeocodeResolver.LatOrLng.LONGITUDE)
            withContext(coroutineProvider.main) {
                propertyInModificationRepository.propertyInModificationLD.value =
                    propertyInModificationRepository.propertyInModificationLD.value
                        ?.copy(
                            propertyLat = newLat,
                            propertyLng = newLng
                        )
            }
        }
    }

    fun onPriceChange(price: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyPrice = price)
    }

    fun onTypeChange(type: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyType = type)
    }

    fun onSurfaceChange(surface: CharSequence?) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertySurface = surface)
    }

    fun onRoomCountChange(roomCount: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyRoomCount = roomCount)
    }

    fun onBedroomCountChange(bedroomCount: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyBedroomCount = bedroomCount)
    }

    fun onBathroomCountChange(bathroomCount: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyBathroomCount = bathroomCount)
    }

    fun onChipChange(name: String, isChecked: Boolean) {
        val initialPointOfInterestMap =
            propertyInModificationRepository.propertyInModificationLD.value?.propertyPoiMap ?: mutableMapOf()
        initialPointOfInterestMap[PointOfInterest.valueOf(name)] = isChecked
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyPoiMap = initialPointOfInterestMap)
    }

    fun onSoldDateChange(year: Int, month: Int, dayOfMonth: Int) {
        val soldDate = LocalDate.of(year, month, dayOfMonth).format(CUSTOM_DATE_FORMATTER)
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertySoldDate = soldDate)
    }

    fun onClearSoldDate() {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertySoldDate = null)
    }

    fun onResultFromCamera() {
        currentPhotoRepository.currentPhotoLD.value = Pair(PhotoOnEdit(photoUri = tempPhotoUri), null)
        editViewAction.value = LaunchEditor
    }

    fun onResultFromGallery(tempPhotoUri: Uri) {
        val currentTempPhotoUri = fileHelper.createTempPhotoFileFromUriAndGetPathBack(tempPhotoUri)
        currentPhotoRepository.currentPhotoLD.value = Pair(PhotoOnEdit(photoUri = currentTempPhotoUri), null)
        editViewAction.value = LaunchEditor
    }

    fun moveToPosition() {
        currentPhotoRepository.currentPhotoLD.value?.second?.let {
            editViewAction.value = EditPropertyViewAction.MoveRecyclerToPosition(it)
        }
        currentPhotoRepository.currentPhotoLD.value = null
    }

    sealed class EditPropertyViewAction {
        class TakePhotoFromCamera(val tempPhotoUri: String) : EditPropertyViewAction()
        object TakePhotoFromGallery : EditPropertyViewAction()
        object LaunchEditor : EditPropertyViewAction()
        class MoveRecyclerToPosition(val position: Int) : EditPropertyViewAction()
        class DisplayDatePicker(val year: Int, val month: Int, val day: Int) : EditPropertyViewAction()
        object FillInError : EditPropertyViewAction()
        object CloseFragment : EditPropertyViewAction()
        object NoPhotoError : EditPropertyViewAction()
    }

    sealed class EditPropertyEvent {
        object PropertySaved : EditPropertyEvent()
    }
}

data class EditUiState(
    val propertyId: String = UUID.randomUUID().toString(),
    val photoList: MutableList<PhotoOnEdit> = mutableListOf(),
    val addPhotoVisibility: Int = View.VISIBLE,
    val propertyTitle: CharSequence? = null,
    val propertyTitleError: String? = null,
    val propertyDescription: CharSequence? = null,
    val propertyDescriptionError: String? = null,
    val propertyAddress: CharSequence? = null,
    val propertyAddressError: String? = null,
    val propertyCityCode: CharSequence? = null,
    val propertyCityCodeError: String? = null,
    val propertyCityName: CharSequence? = null,
    val propertyCityNameError: String? = null,
    val propertyPrice: CharSequence? = null,
    val propertyPriceError: String? = null,
    val propertyType: CharSequence? = null,
    val propertyTypeError: String? = null,
    val propertySurface: CharSequence? = null,
    val propertySurfaceError: String? = null,
    val propertyRoomCount: CharSequence? = null,
    val propertyBedroomCount: CharSequence? = null,
    val propertyBathroomCount: CharSequence? = null,
    val propertySoldDate: CharSequence? = null,
    val propertyPoiMap: MutableMap<PointOfInterest, Boolean> = mutableMapOf(),
    val propertyCreationDate: String? = null,
    val propertyAgent: String? = null,
    val propertyLat: Double = 0.0,
    val propertyLng: Double = 0.0
)

data class PhotoOnEdit(
    val photoId: String = UUID.randomUUID().toString(),
    val photoTitle: String = "",
    val photoUri: String
)

interface OnPropertySavedListener {
    fun onPropertySaved()
}


