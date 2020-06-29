package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragmentViewModel.EditPropertyViewAction.*
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragmentViewModel.PhotoEditorViewAction.*
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyUiModel.*

import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*

class EditPropertyFragmentViewModel(
    private val propertyRepository: PropertyRepository,
    private val fileHelper: FileHelper,
    private val app: Application
) : ViewModel() {


    private var _EditPropertyUiModelLD = MutableLiveData<EditPropertyUiModel>()
    val editPropertyUiModel: LiveData<EditPropertyUiModel> = _EditPropertyUiModelLD

    var viewAction = SingleLiveEvent<EditPropertyViewAction>()
    var photoEditorViewAction = SingleLiveEvent<PhotoEditorViewAction>()

    lateinit var currentPhoto: PhotoUiModel
    lateinit var currentProperty: Property

    init {
        _EditPropertyUiModelLD.value = EditPropertyUiModel()
    }

    fun takePhotoFromCameraClicked() {
        viewAction.value = TakePhotoFromCamera
    }

    fun onPhotoSelected(uri: String) {
        currentPhoto = PhotoUiModel(photoUri = uri)
        viewAction.value = LaunchEditor
    }

    fun takePhotoFromGalleryClicked() {
        viewAction.value = TakePhotoFromGallery
    }

    fun editPhotoClicked(recyclerPosition: Int) {
        currentPhoto = _EditPropertyUiModelLD.value!!.photoList[recyclerPosition]
        viewAction.value = LaunchEditor
    }


    fun onDeletePhotoInEditor() {
        //DeletePhotoFromList()
        val photoList = _EditPropertyUiModelLD.value!!.photoList
        photoList.remove(currentPhoto)
        _EditPropertyUiModelLD.value = _EditPropertyUiModelLD.value?.copy(photoList = photoList)
        photoEditorViewAction.value = PhotoEditorViewAction.CloseFragment
    }

    fun onSavePhotoInEditor(photoTitle: String) {
        val currentPhotoList = _EditPropertyUiModelLD.value!!.photoList
        if (photoTitle.isEmpty()) {
            photoEditorViewAction.value = TitleEmptyError
            return
        }
        var photoToInsert = currentPhoto.copy(photoTitle = photoTitle)
        if (currentPhoto in currentPhotoList) {
            val indexOfCurrentPhoto = currentPhotoList.indexOf(currentPhoto)
            currentPhotoList.remove(currentPhoto)
            currentPhotoList.add(indexOfCurrentPhoto, photoToInsert)
            _EditPropertyUiModelLD.value = _EditPropertyUiModelLD.value?.copy(photoList = currentPhotoList)
            viewAction.value = MoveRecyclerToPosition(indexOfCurrentPhoto)

        } else {
            photoToInsert = photoToInsert.copy(photoId = UUID.randomUUID().toString())
            currentPhotoList.add(photoToInsert)
            _EditPropertyUiModelLD.value = _EditPropertyUiModelLD.value?.copy(photoList = currentPhotoList)
            viewAction.value = MoveRecyclerToPosition(currentPhotoList.size - 1)
        }
        photoEditorViewAction.value = PhotoEditorViewAction.CloseFragment
    }

    fun soldDatePickerClicked() {
        val date = LocalDate.now()
        viewAction.value = DisplayDatePicker(date.year, date.monthValue, date.dayOfMonth)
    }

    fun setSoldDate(year: Int, month: Int, dayOfMonth: Int) {
        val soldDate = LocalDate.of(year, month, dayOfMonth).toString()
        _EditPropertyUiModelLD.value = _EditPropertyUiModelLD.value?.copy(soldDate = soldDate)
    }

    fun savePropertyClicked(
        propertyListPhoto: List<PhotoUiModel>,
        propertyTitle: String,
        propertyDescription: String,
        propertyAddress: String,
        propertyCityCode: String,
        propertyCityName: String,
        propertyPrice: String,
        propertyType: String,
        propertySurface: String,
        propertyRoomCount: String,
        propertyBedroomCount: String,
        propertyBathroomCount: String,
        hasSchool: Boolean,
        hasTransport: Boolean,
        hasShop: Boolean,
        hasParcs: Boolean,
        hasAirport: Boolean,
        hasDownTown: Boolean,
        hasCountrySide: Boolean,
        propertySoldDate: String
    ) {
        _EditPropertyUiModelLD.value =
            EditPropertyUiModel(
                photoList = propertyListPhoto as MutableList<PhotoUiModel>,
                propertyTitle = propertyTitle,
                propertyDescription = propertyDescription,
                propertyAddress = propertyAddress,
                propertyCityCode = propertyCityCode,
                propertyCityName = propertyCityName,
                propertyPrice = propertyPrice,
                propertyType = propertyType,
                propertySurface = propertySurface,
                propertyRoomCount = propertyRoomCount,
                propertyBedroomCount = propertyBedroomCount,
                propertyBathroomCount = propertyBathroomCount,
                propertyPoiSchoolIsChecked = hasSchool,
                propertyPoiTransportIsChecked = hasTransport,
                propertyPoiShopIsChecked = hasShop,
                propertyPoiParcIsChecked = hasParcs,
                propertyPoiAirportIsChecked = hasAirport,
                propertyPoiDownTownIsChecked = hasDownTown,
                propertyPoiCountrySideIsChecked = hasCountrySide,
                soldDate = propertySoldDate
            )

        if (!allInfoCorrect()) {
            viewAction.value = FillInError
            return
        }
        saveProperty()
    }

    private fun allInfoCorrect(): Boolean {
        var allInfoCorrect = true
        val model: EditPropertyUiModel = _EditPropertyUiModelLD.value!!
        with(model) {
            if (photoList.isEmpty()) {
                allInfoCorrect = false
            }
            if (propertyTitle.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertyTitleError = app.resources.getString(R.string.property_title_error))
            }
            if (propertyDescription.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertyDescriptionError = app.resources.getString(R.string.property_description_error))
            }

            if (this.propertyAddress.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertyAddressError = app.resources.getString(R.string.property_address_error))
            }
            if (this.propertyCityCode.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertyCityCodeError = app.resources.getString(R.string.property_city_code_error))
            }
            if (propertyCityName.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertyCityNameError = app.resources.getString(R.string.property_city_name_error))
            }
            if (propertyPrice.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertyPriceError = app.resources.getString(R.string.property_price_error))
            }
            if (propertyType.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertyTypeError = app.resources.getString(R.string.property_type_error))
            }
            if (propertySurface.isEmpty()) {
                allInfoCorrect = false
                _EditPropertyUiModelLD.value =
                    _EditPropertyUiModelLD.value?.copy(propertySurfaceError = app.resources.getString(R.string.property_surface_error))
            }
        }

        return allInfoCorrect
    }


    private fun saveProperty() {
        val propertyToInsert = fromModelToProperty()
        checkInsertOrDeletePhoto(_EditPropertyUiModelLD.value!!.photoList,propertyToInsert)
        viewModelScope.launch(IO) {
            propertyRepository.insertLocalProperty(propertyToInsert)
        }
        viewAction.value = EditPropertyViewAction.CloseFragment
    }

    private fun fromModelToProperty(): Property {
        val poiList = with(_EditPropertyUiModelLD.value) {
            createPoiList(
                this!!.propertyPoiSchoolIsChecked,
                propertyPoiTransportIsChecked,
                propertyPoiShopIsChecked,
                propertyPoiParcIsChecked,
                propertyPoiAirportIsChecked,
                propertyPoiDownTownIsChecked,
                propertyPoiCountrySideIsChecked
            )
        }
        val photoList = createPhotoList(_EditPropertyUiModelLD.value!!.photoList)

        with(_EditPropertyUiModelLD.value) {
            return Property(
                this!!.propertyId,
                propertyTitle,
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
                poiList,
                soldDate
            )
        }


    }

    private fun createPhotoList(photoUiModelList : List<PhotoUiModel>) : List<Photo> {
        val listToReturn : MutableList<Photo> = mutableListOf()
        photoUiModelList.forEach {
            listToReturn.add(Photo(it.photoId,it.photoTitle))
        }
        return listToReturn
    }


    private fun checkInsertOrDeletePhoto(listPhotoUiModel: List<PhotoUiModel>,property : Property) {
        listPhotoUiModel.forEach {
            if (!fileHelper.fileExists(it.photoUri)) {
                fileHelper.saveImageToPropertyFolder(it.photoUri, property.propertyId, it.photoId)
            }
        }
        fileHelper.deleteOldPhotosFromPropertyDirectory(listPhotoUiModel,property.propertyId)
        fileHelper.deleteCache()
    }

    private fun createPoiList(
        hasSchool: Boolean,
        hasTransport: Boolean,
        hasShop: Boolean,
        hasParcs: Boolean,
        hasAirport: Boolean,
        hasDownTown: Boolean,
        hasCountrySide: Boolean
    ): List<PointOfInterest> {
        val poiList = mutableListOf<PointOfInterest>()
        if (hasSchool) {
            poiList.add(PointOfInterest.SCHOOL)
        }
        if (hasTransport) {
            poiList.add(PointOfInterest.TRANSPORT)
        }
        if (hasShop) {
            poiList.add(PointOfInterest.SHOP)
        }
        if (hasParcs) {
            poiList.add(PointOfInterest.PARCS)
        }
        if (hasAirport) {
            poiList.add(PointOfInterest.AIRPORT)
        }
        if (hasDownTown) {
            poiList.add(PointOfInterest.DOWNTOWN)
        }
        if (hasCountrySide) {
            poiList.add(PointOfInterest.COUNTRYSIDE)
        }
        return poiList
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

data class EditPropertyUiModel(
    val propertyId: String = UUID.randomUUID().toString(),
    val photoList: MutableList<PhotoUiModel> = mutableListOf(),
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
    val soldDate: String = "",
    val propertyPoiSchoolIsChecked: Boolean = false,
    val propertyPoiTransportIsChecked: Boolean = false,
    val propertyPoiShopIsChecked: Boolean = false,
    val propertyPoiParcIsChecked: Boolean = false,
    val propertyPoiAirportIsChecked: Boolean = false,
    val propertyPoiDownTownIsChecked: Boolean = false,
    val propertyPoiCountrySideIsChecked: Boolean = false
    )
{
    data class PhotoUiModel(
        val photoId: String = UUID.randomUUID().toString(),
        val photoTitle : String = "",
        val photoUri :String
    )
}



