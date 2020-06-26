package com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.NewPropertyViewAction.*
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.PhotoEditorViewAction.CloseFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.PhotoEditorViewAction.TitleEmptyError
import com.sbizzera.real_estate_manager.utils.FAKE_PHOTO_LIST
import com.sbizzera.real_estate_manager.utils.FileHelper
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*

class NewPropertyFragmentViewModel(
    private val propertyRepository: PropertyRepository,
    private val fileHelper : FileHelper
) : ViewModel() {
    private var _UiModelLD = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> = _UiModelLD

    var viewAction = SingleLiveEvent<NewPropertyViewAction>()
    var photoEditorViewAction = SingleLiveEvent<PhotoEditorViewAction>()

    lateinit var currentPhoto: Photo
    lateinit var currentProperty: Property

//    private var photoList = mutableListOf<Photo>()

    private var photoList: MutableList<Photo> = FAKE_PHOTO_LIST


    init {
        _UiModelLD.value = UiModel(photoList, "")
    }

    fun takePhotoFromCameraClicked() {
        viewAction.value = TakePhotoFromCamera
    }

    fun onPhotoSelected(uri: String) {
        currentPhoto = Photo("noId", "", uri)
        viewAction.value = LaunchEditor
    }

    fun takePhotoFromGalleryClicked() {
        viewAction.value = TakePhotoFromGallery
    }

    fun editPhotoClicked(recyclerPosition: Int) {
        currentPhoto = photoList[recyclerPosition]
        viewAction.value = LaunchEditor
    }


    fun onDeletePhotoInEditor() {
        //DeletePhotoFromList()
        photoList.remove(currentPhoto)
        _UiModelLD.value = _UiModelLD.value?.copy(photoList = photoList)
        photoEditorViewAction.value = CloseFragment
    }

    fun onSavePhotoInEditor(photoTitle: String) {
        if (photoTitle.isEmpty()) {
            photoEditorViewAction.value = TitleEmptyError
            return
        }
        var photoToInsert = currentPhoto.copy(title = photoTitle)
        if (currentPhoto in photoList) {
            val indexOfCurrentPhoto = photoList.indexOf(currentPhoto)
            photoList.remove(currentPhoto)
            photoList.add(indexOfCurrentPhoto, photoToInsert)
            _UiModelLD.value = _UiModelLD.value?.copy(photoList = photoList)
            viewAction.value = MoveRecyclerToPosition(indexOfCurrentPhoto)

        } else {
            photoToInsert = photoToInsert.copy(photoId = UUID.randomUUID().toString())
            photoList.add(photoToInsert)
            _UiModelLD.value = _UiModelLD.value?.copy(photoList = photoList)
            viewAction.value = MoveRecyclerToPosition(photoList.size - 1)
        }
        photoEditorViewAction.value = CloseFragment

    }

    fun soldDatePickerClicked() {
        val date = LocalDate.now()
        viewAction.value = DisplayDatePicker(date.year, date.monthValue, date.dayOfMonth)
    }

    fun setSoldDate(year: Int, month: Int, dayOfMonth: Int) {
        val soldDate = LocalDate.of(year, month, dayOfMonth).toString()
        _UiModelLD.value = _UiModelLD.value?.copy(soldDate = soldDate)
    }

    fun savePropertyClicked(
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
        var allFieldsCompleted = true
        if (photoList.isEmpty()) {
            viewAction.value = PhotoListError
            allFieldsCompleted = false
        }
        if (propertyTitle.isNullOrEmpty()) {
            viewAction.value = TitleError
            allFieldsCompleted = false
        }
        if (propertyDescription.isNullOrEmpty()) {
            viewAction.value = DescriptionError
            allFieldsCompleted = false
        }
        if (propertyAddress.isNullOrEmpty()) {
            viewAction.value = AddressError
            allFieldsCompleted = false
        }
        if (propertyCityCode.isNullOrEmpty()) {
            viewAction.value = CityCodeError
            allFieldsCompleted = false
        }
        if (propertyCityName.isNullOrEmpty()) {
            viewAction.value = CityNameError
            allFieldsCompleted = false
        }
        if (propertyPrice.isNullOrEmpty()) {
            viewAction.value = PriceError
            allFieldsCompleted = false
        }
        if (propertyType.isNullOrEmpty()) {
            viewAction.value = TypeError
            allFieldsCompleted = false
        }
        if (propertySurface.isNullOrEmpty()) {
            viewAction.value = SurfaceError
            allFieldsCompleted = false
        }

        if (allFieldsCompleted) {
            saveProperty(
                propertyTitle,
                propertyDescription,
                propertyAddress,
                propertyCityCode,
                propertyCityName,
                propertyPrice,
                propertyType,
                propertySurface,
                propertyRoomCount,
                propertyBedroomCount,
                propertyBathroomCount,
                hasSchool,
                hasTransport,
                hasShop,
                hasParcs,
                hasAirport,
                hasDownTown,
                hasCountrySide,
                propertySoldDate
            )
        } else {
            viewAction.value = FillInError
        }
    }

    private fun saveProperty(
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
        //TODO check previous Property before saving
        //TODO check Photo and insert them
        checkInsertOrDeletePhoto()
        val poiList = createPoiList(hasSchool, hasTransport, hasShop, hasParcs, hasAirport, hasDownTown, hasCountrySide)
        viewModelScope.launch(IO) {
            propertyRepository.insertLocalProperty(
                Property(
                    propertyId = UUID.randomUUID().toString(),
                    propertyTitle = propertyTitle,
                    propertyType = propertyType,
                    price = propertyPrice.toInt(),
                    photoList = photoList,
                    modificationDate = LocalDateTime.now().toString(),
                    propertyDescription = propertyDescription,
                    propertyAddress = propertyAddress,
                    propertyCityCode = propertyCityCode,
                    propertyCityName = propertyCityName,
                    propertySurface = propertySurface.toInt(),
                    propertyRooms = propertyRoomCount.toInt(),
                    propertyBedRooms = propertyBedroomCount.toInt(),
                    propertyBathRooms = propertyBathroomCount.toInt(),
                    propertyPoiList = poiList,
                    soldDate = propertySoldDate
                )
            )
        }
    }

    private fun checkInsertOrDeletePhoto() {
        photoList.forEach {
            if(!fileHelper.fileExists(it.uri)){
                fileHelper.saveImageToPropertyFolder(it.uri,"fakeId",it.photoId)
            }
        }
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


    sealed class NewPropertyViewAction {
        object TakePhotoFromCamera : NewPropertyViewAction()
        object TakePhotoFromGallery : NewPropertyViewAction()
        object LaunchEditor : NewPropertyViewAction()
        class MoveRecyclerToPosition(val position: Int) : NewPropertyViewAction()
        class DisplayDatePicker(val year: Int, val month: Int, val day: Int) : NewPropertyViewAction()
        object PhotoListError : NewPropertyViewAction()
        object TitleError : NewPropertyViewAction()
        object DescriptionError : NewPropertyViewAction()
        object AddressError : NewPropertyViewAction()
        object CityCodeError : NewPropertyViewAction()
        object CityNameError : NewPropertyViewAction()
        object PriceError : NewPropertyViewAction()
        object TypeError : NewPropertyViewAction()
        object SurfaceError : NewPropertyViewAction()
        object FillInError : NewPropertyViewAction()

    }

    sealed class PhotoEditorViewAction {
        object CloseFragment : PhotoEditorViewAction()
        object TitleEmptyError : PhotoEditorViewAction()
    }

}

data class UiModel(
    val photoList: List<Photo>,
    val soldDate: String
)