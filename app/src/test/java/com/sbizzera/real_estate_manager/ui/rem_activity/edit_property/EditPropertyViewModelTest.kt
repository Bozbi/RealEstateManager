package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property

import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.sbizzera.real_estate_manager.application.App
import com.sbizzera.real_estate_manager.data.model.Photo
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.data.repository.*
import com.sbizzera.real_estate_manager.test_utils.getOrAwaitValue
import com.sbizzera.real_estate_manager.utils.CoroutineContextProvider
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import com.sbizzera.real_estate_manager.utils.helper.GeocodeResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.threeten.bp.Clock
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


@ExperimentalCoroutinesApi
class EditPropertyViewModelTest {
    private val currentPropertyIdRepository = CurrentPropertyIdRepository.instance
    private val propertyInModificationRepository = PropertyInModificationRepository.instance
    private lateinit var propertyRepo: PropertyRepository
    private lateinit var fileHelper: FileHelper
    private val currentEditedPhotoRepository = CurrentEditedPhotoRepository.instance
    private lateinit var context: Context
    private lateinit var geocodeResolver: GeocodeResolver
    private lateinit var sharedPreferencesRepo: SharedPreferencesRepo
    private lateinit var clock: Clock

    private lateinit var viewModel: EditPropertyViewModel

    private val mockedProperty = MutableLiveData(Property())
    private var mockedInsertReturn = 10L

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val coroutineContextProvider = CoroutineContextProvider(
        testCoroutineDispatcher,
        testCoroutineDispatcher
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(
            testCoroutineDispatcher
        )

        propertyRepo = mock {
            onBlocking { insertLocalProperty(anyObject()) } doReturn  (mockedInsertReturn)
        }
        `when`(propertyRepo.getPropertyByIdLD(anyString())).thenReturn(mockedProperty)

        fileHelper = mock(FileHelper::class.java)
        `when`(fileHelper.getUriFromFileName(anyString(), anyString()))
            .thenAnswer { invocation ->
                "${invocation.arguments[0]}/${invocation.arguments[1]}.mock"
            }
        `when`(fileHelper.createEmptyTempPhotoFileAndGetUriBack()).thenReturn(
            "tempPhotoUriMocked"
        )

        context = mock(App::class.java)

        geocodeResolver = mock(GeocodeResolver::class.java)

        sharedPreferencesRepo = mock(SharedPreferencesRepo::class.java)

        clock = Clock.fixed(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant(), ZoneOffset.UTC)


        currentPropertyIdRepository.currentPropertyIdLiveData.value = "id1"


        viewModel = EditPropertyViewModel(
            currentPropertyIdRepository,
            propertyInModificationRepository,
            propertyRepo,
            fileHelper,
            currentEditedPhotoRepository,
            context,
            geocodeResolver,
            sharedPreferencesRepo,
            clock,
            coroutineContextProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun shouldReturnProperEditUiState() {
        mockedProperty.value = property1
        val model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals(
            editUiState, model
        )
    }

    @Test
    fun shouldLaunchIntentOnTakePhotoCameraClicked() {
        viewModel.takePhotoFromCameraClicked()
        val model = viewModel.editViewAction.getOrAwaitValue()
        assertEquals(
            "tempPhotoUriMocked",
            (model as EditPropertyViewModel.EditPropertyViewAction.TakePhotoFromCamera).tempPhotoUri
        )
    }

    @Test
    fun shouldLaunchDatePickerOnSoldDateClicked() {
        viewModel.soldDatePickerClicked(2020, 1, 1)
        val model = viewModel.editViewAction.getOrAwaitValue()
        assertEquals(
            2020
            , (model as EditPropertyViewModel.EditPropertyViewAction.DisplayDatePicker).year
        )
        assertEquals(
            1
            , model.month
        )
        assertEquals(
            1
            , model.day
        )
    }

    @Test
    fun shouldLaunchCameraGalleryOnGalleryClick() {
        viewModel.takePhotoFromGalleryClicked()
        val model = viewModel.editViewAction.getOrAwaitValue()
        assertEquals(
            EditPropertyViewModel.EditPropertyViewAction.TakePhotoFromGallery, model
        )
    }

    @Test
    fun shouldTriggerNoPhotoErrorIfPropertyHasNoPhotoOnSave() {
        viewModel.editUiStateLD.getOrAwaitValue()
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value!!.copy(
                propertyPrice = "",
                propertySurface = ""
            )
        viewModel.savePropertyClicked()
        val model = viewModel.editViewAction.getOrAwaitValue()
        assertEquals(EditPropertyViewModel.EditPropertyViewAction.NoPhotoError, model)
    }

    @Test
    fun shouldTriggerFillErrorIfPropertyHasPhotoButInfoNotCorrectOnSave() {
        viewModel.editUiStateLD.getOrAwaitValue()
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value!!.copy(
                propertyPrice = "",
                propertySurface = "",
                photoList = mutableListOf(
                    PhotoOnEdit(
                        "test",
                        "test",
                        "test"
                    )
                )

            )
        viewModel.savePropertyClicked()
        val model = viewModel.editViewAction.getOrAwaitValue()
        assertEquals(EditPropertyViewModel.EditPropertyViewAction.FillInError, model)
    }

    @Test
    fun onSavePropertyShouldNotSaveIfPropertyNotModified() {
        mockedProperty.value = property1
        viewModel.editUiStateLD.getOrAwaitValue()
        viewModel.savePropertyClicked()
        val viewActionModel = viewModel.editViewAction.getOrAwaitValue()
        assertEquals(EditPropertyViewModel.EditPropertyViewAction.CloseFragment, viewActionModel)

    }

    @Test
    fun shouldSavePropertyIfAllInfoCorrectAndPropertyHasChanged() = runBlockingTest {
        mockedProperty.value = property1
        viewModel.editUiStateLD.getOrAwaitValue()
        propertyInModificationRepository.propertyInModificationLD.value =
            propertyInModificationRepository.propertyInModificationLD.value!!.copy(
                propertyTitle = "NewTitle"
            )
        viewModel.savePropertyClicked()
        val viewActionModel = viewModel.editViewAction.getOrAwaitValue()
        val viewEventModel = viewModel.editEvent.getOrAwaitValue()
        assertEquals(EditPropertyViewModel.EditPropertyViewAction.CloseFragment, viewActionModel)
        assertEquals(EditPropertyViewModel.EditPropertyEvent.PropertySaved, viewEventModel)
    }

    @Test
    fun editPhotoClickShouldChangeRepoPositionAndTriggerPhotoEditor() {
        mockedProperty.value = property1
        viewModel.editUiStateLD.getOrAwaitValue()
        viewModel.editPhotoClicked(1)
        val model = viewModel.editViewAction.getOrAwaitValue()
        assertEquals(EditPropertyViewModel.EditPropertyViewAction.LaunchEditor,model)
        assertEquals(1,currentEditedPhotoRepository.currentPhotoLD.value?.second)
    }

    @Test
    fun onTitleChangeShouldChangeState(){
        mockedProperty.value = property1
        viewModel.editUiStateLD.getOrAwaitValue()
        viewModel.onTitleChange("Test")
        var model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("Test",model.propertyTitle)

        viewModel.onDescriptionChange("Test")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("Test",model.propertyDescription)

        viewModel.onAddressChange("TestAddress")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("TestAddress",model.propertyAddress)

        viewModel.onCityCodeChange("TestCityCode")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("TestCityCode",model.propertyCityCode)

        viewModel.onCityNameChange("TestCityName")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("TestCityName",model.propertyCityName)

        viewModel.onPriceChange("123")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("123",model.propertyPrice)

        viewModel.onTypeChange("Mansion")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("Mansion",model.propertyType)

        viewModel.onSurfaceChange("124")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("124",model.propertySurface)

        viewModel.onRoomCountChange("127")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("127",model.propertyRoomCount)

        viewModel.onBedroomCountChange("128")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("128",model.propertyBedroomCount)

        viewModel.onBathroomCountChange("129")
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("129",model.propertyBathroomCount)

        viewModel.onChipChange("COUNTRYSIDE",true)
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals(true,model.propertyPoiMap[PointOfInterest.COUNTRYSIDE])

        viewModel.onSoldDateChange(2019,12,12)
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals("12/12/2019",model.propertySoldDate)

        viewModel.onClearSoldDate()
        model = viewModel.editUiStateLD.getOrAwaitValue()
        assertEquals(null,model.propertySoldDate)
    }



    private var property1 = Property(
        "id1",
        "Title1",
        "House",
        100,
        listOf(
            Photo("photo1inProperty1", "photo1inProperty1Title"),
            Photo("photo2inProperty1", "photo2inProperty1Title")
        ),
        "23/11/2020T12:17",
        "Property1Description",
        "address1",
        "cityCode1",
        "cityName1",
        10,
        11,
        12,
        13,
        listOf(
            PointOfInterest.AIRPORT,
            PointOfInterest.COUNTRYSIDE
        ),
        "",
        "2020-01-01T01:00:00.000",
        1.0,
        2.0,
        "Bobo"
    )

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
@Suppress("DEPRECATION")
private fun <T> anyObject(): T {
    Mockito.anyObject<T>()
    return uninitialized()
}
@Suppress("UNCHECKED_CAST")
private fun <T> uninitialized(): T = null as T