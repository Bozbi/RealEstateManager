package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.PropertyInModificationRepository
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.utils.CUSTOM_DATE_FORMATTER
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*
import kotlin.collections.set

class EditPropertyViewModel(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val propertyInModificationRepository: PropertyInModificationRepository,
    private val propertyRepository: PropertyRepository,
    private val fileHelper: FileHelper,
    private val app: Application
) : ViewModel() {

    val editViewAction = SingleLiveEvent<EditPropertyViewAction>()
    val editUiStateLD: LiveData<EditUiState>

    val photoEditorViewAction = SingleLiveEvent<PhotoEditorViewAction>()
    lateinit var currentPhoto: EditUiState.PhotoInEditUiState

    init {

        val currentPropertyId = currentPropertyIdRepository.currentPropertyIdLiveData.value
        if (currentPropertyId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val property = propertyRepository.getPropertyByIdAsync(currentPropertyId)
                withContext(Dispatchers.Main) {
                    val editUiState = fromPropertyToEditUiState(property, fileHelper)
                    propertyInModificationRepository.propertyInModificationLD.value = editUiState
                }
            }
        }else{
            propertyInModificationRepository.propertyInModificationLD.value = EditUiState()
        }
        editUiStateLD = propertyInModificationRepository.propertyInModificationLD
    }


    fun takePhotoFromCameraClicked() {
        editViewAction.value = EditPropertyViewAction.TakePhotoFromCamera
    }

    fun takePhotoFromGalleryClicked() {
        editViewAction.value = EditPropertyViewAction.TakePhotoFromGallery
    }

    fun soldDatePickerClicked() {
        val date = LocalDate.now()
        editViewAction.value =
            EditPropertyViewAction.DisplayDatePicker(date.year, date.monthValue, date.dayOfMonth)
    }

//    fun savePropertyClicked(
//        propertyListPhoto: List<EditUiState.PhotoInEditUiState>,
//        propertyTitle: String,
//        propertyDescription: String,
//        propertyAddress: String,
//        propertyCityCode: String,
//        propertyCityName: String,
//        propertyPrice: String,
//        propertyType: String,
//        propertySurface: String,
//        propertyRoomCount: String,
//        propertyBedroomCount: String,
//        propertyBathroomCount: String,
//        hasSchool: Boolean,
//        hasTransport: Boolean,
//        hasShop: Boolean,
//        hasParcs: Boolean,
//        hasAirport: Boolean,
//        hasDownTown: Boolean,
//        hasCountrySide: Boolean,
//        propertySoldDate: String
//    ) {
//        editUiStateModifiedLD.value =
//            editUiStateModifiedLD.value?.copy(
//                photoList = propertyListPhoto as MutableList<EditUiState.PhotoInEditUiState>,
//                propertyTitle = propertyTitle,
//                propertyDescription = propertyDescription,
//                propertyAddress = propertyAddress,
//                propertyCityCode = propertyCityCode,
//                propertyCityName = propertyCityName,
//                propertyPrice = propertyPrice,
//                propertyType = propertyType,
//                propertySurface = propertySurface,
//                propertyRoomCount = propertyRoomCount,
//                propertyBedroomCount = propertyBedroomCount,
//                propertyBathroomCount = propertyBathroomCount,
//                propertySoldDate = propertySoldDate
//
//            )
//        if (!allInfoCorrect()) {
//            editViewAction.value = EditPropertyViewAction.FillInError
//            return
//        }
//        saveProperty()
//    }

//    private fun allInfoCorrect(): Boolean {
//        var allInfoCorrect = true
//        val state = editUiStateModifiedLD.value!!
//        state.let {
//            with(it) {
//
//                if (photoList.isEmpty()) {
//                    allInfoCorrect = false
//                }
//                if (propertyTitle.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateLD.value?.copy(
//                            propertyTitleError = app.resources.getString(
//                                R.string.property_title_error
//                            )
//                        )
//                }
//                if (propertyDescription.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateModifiedLD.value?.copy(
//                            propertyDescriptionError = app.resources.getString(
//                                R.string.property_description_error
//                            )
//                        )
//                }
//
//                if (this.propertyAddress.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateModifiedLD.value?.copy(
//                            propertyAddressError = app.resources.getString(
//                                R.string.property_address_error
//                            )
//                        )
//                }
//                if (this.propertyCityCode.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateModifiedLD.value?.copy(
//                            propertyCityCodeError = app.resources.getString(
//                                R.string.property_city_code_error
//                            )
//                        )
//                }
//                if (propertyCityName.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateModifiedLD.value?.copy(
//                            propertyCityNameError = app.resources.getString(
//                                R.string.property_city_name_error
//                            )
//                        )
//                }
//                if (propertyPrice.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateModifiedLD.value?.copy(
//                            propertyPriceError = app.resources.getString(
//                                R.string.property_price_error
//                            )
//                        )
//                }
//                if (propertyType.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateModifiedLD.value?.copy(
//                            propertyTypeError = app.resources.getString(
//                                R.string.property_type_error
//                            )
//                        )
//                }
//                if (propertySurface.isEmpty()) {
//                    allInfoCorrect = false
//                    this@EditPropertyViewModel.editUiStateModifiedLD.value =
//                        this@EditPropertyViewModel.editUiStateModifiedLD.value?.copy(
//                            propertySurfaceError = app.resources.getString(
//                                R.string.property_surface_error
//                            )
//                        )
//                }
//            }
//        }
//
//        return allInfoCorrect
//    }

//    private fun saveProperty() {
//        val propertyToInsert = fromEditUiStateToProperty()
//        checkInsertOrDeletePhoto(editUiStateModifiedLD.value!!.photoList, propertyToInsert)
//        propertyRepository.insertLocalProperty(propertyToInsert)
//        editViewAction.value = EditPropertyViewAction.CloseFragment
//    }

    private fun checkInsertOrDeletePhoto(listPhotoUiModel: List<EditUiState.PhotoInEditUiState>, property: Property) {
        listPhotoUiModel.forEach {
            if (!fileHelper.fileExists(it.photoUri)) {
                fileHelper.saveImageToPropertyFolder(it.photoUri, property.propertyId, it.photoId)
            }
        }
        fileHelper.deleteOldPhotosFromPropertyDirectory(listPhotoUiModel, property.propertyId)
        fileHelper.deleteCache()
    }


    fun onPhotoSelected(uri: String) {
        currentPhoto = EditUiState.PhotoInEditUiState(photoUri = uri)
        editViewAction.value = EditPropertyViewAction.LaunchEditor
    }

    fun editPhotoClicked(recyclerPosition: Int) {
        currentPhoto = this.editUiStateLD.value!!.photoList[recyclerPosition]
        editViewAction.value = EditPropertyViewAction.LaunchEditor
    }

    private fun fromEditUiStateToProperty(): Property {
        val photoList = createPhotoList(this.editUiStateLD.value!!.photoList)
        with(this.editUiStateLD.value) {
            return Property(
                this!!.propertyId,
                propertyTitle.capitalize(),
                propertyType,
                propertyPrice.toIntOrNull() ?: 0,
                photoList,
                LocalDateTime.now().toString(),
                propertyDescription,
                propertyAddress,
                propertyCityCode,
                propertyCityName,
                propertySurface.toIntOrNull() ?: 0,
                propertyRoomCount.toIntOrNull() ?: 0,
                propertyBedroomCount.toIntOrNull() ?: 0,
                propertyBathroomCount.toIntOrNull() ?: 0,
                createPoiList(propertyPoiMap),
                propertySoldDate,
                LocalDateTime.now().format(CUSTOM_DATE_FORMATTER)
            )
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
                propertyPoiMap = createPoiMap(propertyPoiList)
            )
        }
    }

    private fun createPoiMap(
        poiList: List<PointOfInterest>
    ): MutableMap<String, Boolean> {
        val propertyPoiMap = mutableMapOf<String, Boolean>()
        poiList.forEach {
            propertyPoiMap[it.value] = true
        }
        return propertyPoiMap
    }

    private fun createPoiList(
        poiMap: Map<String, Boolean>
    ): List<PointOfInterest> {
        val poiListToReturn = mutableListOf<PointOfInterest>()
        poiMap.forEach {
            poiListToReturn.add(PointOfInterest.valueOf(it.key))
        }
        return poiListToReturn
    }

    private fun createPhotoList(photoUiModelList: List<EditUiState.PhotoInEditUiState>): List<Photo> {
        val listToReturn: MutableList<Photo> = mutableListOf()
        photoUiModelList.forEach {
            listToReturn.add(Photo(it.photoId, it.photoTitle))
        }
        return listToReturn
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  PhotoEditorFragment Methods
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    fun onDeletePhotoInEditor() {
//        //DeletePhotoFromList()
//        val photoList = this.editUiStateLD.value!!.photoList
//        photoList.remove(currentPhoto)
//        editUiStateModifiedLD.value = this.editUiStateLD.value?.copy(photoList = photoList)
//        photoEditorViewAction.value =
//            CloseFragment
//    }
//
//    fun onSavePhotoInEditor(photoTitle: String) {
//        val currentPhotoList = this.editUiStateLD.value!!.photoList
//        if (photoTitle.isEmpty()) {
//            photoEditorViewAction.value = TitleEmptyError
//            return
//        }
//        var photoToInsert = currentPhoto.copy(photoTitle = photoTitle)
//        if (currentPhoto in currentPhotoList) {
//            val indexOfCurrentPhoto = currentPhotoList.indexOf(currentPhoto)
//            currentPhotoList.remove(currentPhoto)
//            currentPhotoList.add(indexOfCurrentPhoto, photoToInsert)
//            editUiStateModifiedLD.value = this.editUiStateLD.value?.copy(photoList = currentPhotoList)
//            editViewAction.value =
//                MoveRecyclerToPosition(indexOfCurrentPhoto)
//
//        } else {
//            photoToInsert = photoToInsert.copy(photoId = UUID.randomUUID().toString())
//            currentPhotoList.add(photoToInsert)
//            editUiStateModifiedLD.value = this.editUiStateLD.value?.copy(photoList = currentPhotoList)
//            editViewAction.value =
//                MoveRecyclerToPosition(currentPhotoList.size - 1)
//        }
//        photoEditorViewAction.value =
//            CloseFragment
//    }

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
    }

    fun onCityCodeChange(cityCode: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyCityCode = cityCode)
    }

    fun onCityNameChange(cityName: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyCityName = cityName)
    }

    fun onPriceChange(price: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyPrice = price)
    }

    fun onTypeChange(type: String) {
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyType = type)
    }

    fun onSurfaceChange(surface: String) {
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

    fun onChipChange(tag: String, isChecked: Boolean) {
        val initialPointOfInterestMap =
            propertyInModificationRepository.propertyInModificationLD.value?.propertyPoiMap ?: mutableMapOf()
        initialPointOfInterestMap[tag] = isChecked
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertyPoiMap = initialPointOfInterestMap)
    }

    fun onSoldDateChange(year: Int, month: Int, dayOfMonth: Int) {
        val soldDate = LocalDate.of(year, month, dayOfMonth).format(CUSTOM_DATE_FORMATTER)
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value?.copy(propertySoldDate = soldDate)
    }

    sealed class EditPropertyViewAction {
        object TakePhotoFromCamera : EditPropertyViewAction()
        object TakePhotoFromGallery : EditPropertyViewAction()
        object LaunchEditor : EditPropertyViewAction()
        class MoveRecyclerToPosition(val position: Int) : EditPropertyViewAction()
        class DisplayDatePicker(val year: Int, val month: Int, val day: Int) : EditPropertyViewAction()
        object FillInError : EditPropertyViewAction()
        object CloseFragment : EditPropertyViewAction()
    }

    sealed class PhotoEditorViewAction {
        object CloseFragment : PhotoEditorViewAction()
        object TitleEmptyError : PhotoEditorViewAction()
    }
}

data class EditUiState(
    val propertyId: String = UUID.randomUUID().toString(),
    val photoList: MutableList<PhotoInEditUiState> = mutableListOf(),
    val propertyTitle: String = "",
    val propertyTitleError: String? = null,
    val propertyDescription: String = "",
    val propertyDescriptionError: String? = null,
    val propertyAddress: String = "",
    val propertyAddressError: String? = null,
    val propertyCityCode: String = "",
    val propertyCityCodeError: String? = null,
    val propertyCityName: String = "",
    val propertyCityNameError: String? = null,
    val propertyPrice: String = "",
    val propertyPriceError: String? = null,
    val propertyType: String = "",
    val propertyTypeError: String? = null,
    val propertySurface: String = "",
    val propertySurfaceError: String? = null,
    val propertyRoomCount: String = "",
    val propertyBedroomCount: String = "",
    val propertyBathroomCount: String = "",
    val propertySoldDate: String = "",
    val propertyPoiMap: MutableMap<String, Boolean> = mutableMapOf()
) {
    data class PhotoInEditUiState(
        val photoId: String = UUID.randomUUID().toString(),
        val photoTitle: String = "",
        val photoUri: String
    )
}

fun List<Photo>.toPhotoInEditUiState(
    fileHelper: FileHelper,
    propertyId: String
): MutableList<EditUiState.PhotoInEditUiState> {;
    val listToReturn = mutableListOf<EditUiState.PhotoInEditUiState>()
    forEach {
        listToReturn.add(
            EditUiState.PhotoInEditUiState(
                photoId = it.photoId,
                photoTitle = it.title,
                photoUri = fileHelper.getUriFromFileName(it.photoId, propertyId)
            )
        )
    }
    return listToReturn
}