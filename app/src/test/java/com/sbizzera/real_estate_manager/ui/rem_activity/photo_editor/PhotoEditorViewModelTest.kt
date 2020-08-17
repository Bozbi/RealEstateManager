package com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor


import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.data.repository.CurrentEditedPhotoRepository
import com.sbizzera.real_estate_manager.data.repository.PropertyInModificationRepository
import com.sbizzera.real_estate_manager.test_utils.getOrAwaitValue
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property.EditUiState
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property.PhotoOnEdit
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PhotoEditorViewModelTest{

    private lateinit var viewModel: PhotoEditorViewModel
    private val currentEditedPhotoRepository = CurrentEditedPhotoRepository.instance
    private val propertyInModificationRepository = PropertyInModificationRepository.instance

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        propertyInModificationRepository.propertyInModificationLD.value = editUiState
        currentEditedPhotoRepository.currentPhotoLD.value = Pair(editUiState.photoList[0],0)
        viewModel = PhotoEditorViewModel(
            currentEditedPhotoRepository,
            propertyInModificationRepository
        )
    }

    @Test
    fun onDeletePhotoInEditor(){
        propertyInModificationRepository.propertyInModificationLD.value = editUiState
        currentEditedPhotoRepository.currentPhotoLD.value = Pair(editUiState.photoList[0],0)

        viewModel.onDeletePhotoInEditor()

        val state = propertyInModificationRepository.propertyInModificationLD.value

        val viewAction = viewModel.photoEditorViewAction.getOrAwaitValue()
        assertEquals(1,state?.photoList?.size)
        assertEquals(PhotoEditorViewModel.PhotoEditorViewAction.CloseFragment,viewAction)
    }

    @Test
    fun onSavePhotoInEditorShouldReturnErrorIfEmpty(){
        viewModel.onSavePhotoInEditor("")
        val viewAction = viewModel.photoEditorViewAction.getOrAwaitValue()
        assertEquals(PhotoEditorViewModel.PhotoEditorViewAction.TitleEmptyError,viewAction)
    }

    @Test
    fun onSavePhotoInEditorWithSameTitleShouldReturnWithoutSaving(){
        viewModel.onSavePhotoInEditor("photo1inProperty1Title")
        val viewAction = viewModel.photoEditorViewAction.getOrAwaitValue()
        assertEquals(PhotoEditorViewModel.PhotoEditorViewAction.CloseFragment,viewAction)
    }


    var editUiState = EditUiState(
        propertyId = "id1",
        photoList = mutableListOf(
            PhotoOnEdit(
                photoId = "photo1inProperty1",
                photoTitle = "photo1inProperty1Title",
                photoUri = "photo1inProperty1/id1.mock"
            ),
            PhotoOnEdit(
                photoId = "photo2inProperty1",
                photoTitle = "photo2inProperty1Title",
                photoUri = "photo2inProperty1/id1.mock"
            )
        ),
        addPhotoVisibility = View.INVISIBLE,
        propertyTitle = "Title1",
        propertyTitleError = null,
        propertyDescription = "Property1Description",
        propertyDescriptionError = null,
        propertyAddress = "address1",
        propertyAddressError = null,
        propertyCityCode = "cityCode1",
        propertyCityCodeError = null,
        propertyCityName = "cityName1",
        propertyCityNameError = null,
        propertyPrice = "100",
        propertyPriceError = null,
        propertyType = "House",
        propertyTypeError = null,
        propertySurface = "10",
        propertySurfaceError = null,
        propertyRoomCount = "11",
        propertyBedroomCount = "12",
        propertyBathroomCount = "13",
        propertySoldDate = "",
        propertyPoiMap = mutableMapOf(
            PointOfInterest.AIRPORT to true,
            PointOfInterest.COUNTRYSIDE to true
        ),
        propertyCreationDate = "2020-01-01T01:00:00.000",
        propertyAgent = "Bobo",
        propertyLat = 1.0,
        propertyLng = 2.0
    )
}