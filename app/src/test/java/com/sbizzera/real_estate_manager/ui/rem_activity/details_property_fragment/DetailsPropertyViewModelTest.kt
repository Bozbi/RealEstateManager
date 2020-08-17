package com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.data.model.Photo
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.data.repository.CurrentPhotoPositionRepo
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.repository.PropertyRepository
import com.sbizzera.real_estate_manager.test_utils.getOrAwaitValue
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito

class DetailsPropertyViewModelTest {

    private lateinit var propertyRepo: PropertyRepository
    private val currentPropertyIdRepository = CurrentPropertyIdRepository.instance
    private val currentPhotoPositionRepository = CurrentPhotoPositionRepo.instance
    private lateinit var fileHelper: FileHelper
    private lateinit var viewModel: DetailsPropertyViewModel

    private val mockedProperty = MutableLiveData<Property>()

    @Mock
    private lateinit var context: Context

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        propertyRepo = Mockito.mock(PropertyRepository::class.java)
        fileHelper = Mockito.mock(FileHelper::class.java)
        context = Mockito.mock(Context::class.java)

        Mockito.`when`(propertyRepo.getPropertyByIdLD(ArgumentMatchers.anyString())).thenReturn(mockedProperty)

        Mockito.`when`(fileHelper.getUriFromFileName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenAnswer { invocation ->
                "${invocation.arguments[0]}/${invocation.arguments[1]}.mock"
            }


        currentPropertyIdRepository.currentPropertyIdLiveData.value = "id1"

        viewModel = DetailsPropertyViewModel(
            propertyRepo,
            currentPropertyIdRepository,
            currentPhotoPositionRepository,
            fileHelper
        )
    }

    @Test
    fun modifyPropertyClickedShouldTriggerEditPropertyViewAction() {
        viewModel.modifyPropertyClicked()
        assertEquals(DetailsPropertyViewModel.DetailsViewAction.ModifyPropertyClicked, viewModel.detailsViewAction.value)
    }

    @Test
    fun onViewBoundReadyShouldTriggerAction() {
        viewModel.onViewHolderBound(currentPhotoPositionRepository.currentPhotoPosition)
        assertEquals(DetailsPropertyViewModel.DetailsViewAction.ViewHolderReady, viewModel.detailsViewAction.value)
    }

    @Test
    fun stateShouldMatchProperty() {
        mockedProperty.value = property1

        val model = viewModel.detailsUiStateLD.getOrAwaitValue()

        assertEquals(
            DetailsUiState(
                "title1",
                "$100",


                "- House -",
                "available since 01/01/2020",
                listOf(
                    DetailsPhotoUiState("photo1inProperty1Title", "photo1inProperty1/id1.mock"),
                    DetailsPhotoUiState("photo2inProperty1Title", "photo2inProperty1/id1.mock")
                ),
                "10",
                "11",
                "12",
                "13",
                "Property1Description",
                "address1\ncityCode1 cityName1",
                "Near airport, countryside.",
                "https://maps.googleapis.com/maps/api/staticmap?center=address1+cityCode1+cityName1&markers=color:blue%7Caddress1+cityCode1+cityName1&zoom=19&size=640x640&key=AIzaSyBj5qmVCaJg5fBSzF1udTUajB1sWp0Nnow",
                "added by Bobo"
            ), model
        )

    }

    @Test
    fun shouldReturnEmptyDetailsIfPropertyNull() {
        mockedProperty.value = null
        val model = viewModel.detailsUiStateLD.getOrAwaitValue()

        assertEquals(DetailsUiState(), model)

    }

    @Test
    fun shouldReturnSoldDateIfFilled() {
        mockedProperty.value = property1.copy(
            soldDate = "10/10/2010"
        )
        val model = viewModel.detailsUiStateLD.getOrAwaitValue()
        assertEquals(
            DetailsUiState(
                "title1",
                "$100",


                "- House -",
                "sold on 10/10/2010",
                listOf(
                    DetailsPhotoUiState("photo1inProperty1Title", "photo1inProperty1/id1.mock"),
                    DetailsPhotoUiState("photo2inProperty1Title", "photo2inProperty1/id1.mock")
                ),
                "10",
                "11",
                "12",
                "13",
                "Property1Description",
                "address1\ncityCode1 cityName1",
                "Near airport, countryside.",
                "https://maps.googleapis.com/maps/api/staticmap?center=address1+cityCode1+cityName1&markers=color:blue%7Caddress1+cityCode1+cityName1&zoom=19&size=640x640&key=AIzaSyBj5qmVCaJg5fBSzF1udTUajB1sWp0Nnow",
                "added by Bobo"
            ), model
        )
    }

    @Test
    fun shouldReturnAppropriatePoiTextIfNull(){
        mockedProperty.value = property1.copy(
            propertyPoiList = listOf()
        )
        val model = viewModel.detailsUiStateLD.getOrAwaitValue()
        assertEquals(
            DetailsUiState(
                "title1",
                "$100",


                "- House -",
                "available since 01/01/2020",
                listOf(
                    DetailsPhotoUiState("photo1inProperty1Title", "photo1inProperty1/id1.mock"),
                    DetailsPhotoUiState("photo2inProperty1Title", "photo2inProperty1/id1.mock")
                ),
                "10",
                "11",
                "12",
                "13",
                "Property1Description",
                "address1\ncityCode1 cityName1",
                "no known points of interest",
                "https://maps.googleapis.com/maps/api/staticmap?center=address1+cityCode1+cityName1&markers=color:blue%7Caddress1+cityCode1+cityName1&zoom=19&size=640x640&key=AIzaSyBj5qmVCaJg5fBSzF1udTUajB1sWp0Nnow",
                "added by Bobo"
            ), model
        )
    }

    @Test
    fun shouldChangeCurrentPhotoPositionOnPhotoClicked(){
        viewModel.photoClicked(2)
        assertEquals(
            2,currentPhotoPositionRepository.currentPhotoPosition
        )
    }

    @Test
    fun shouldGetGoodCurrentPhotoPosition(){
        assertEquals(viewModel.getCurrentPhotoPosition(),currentPhotoPositionRepository.currentPhotoPosition)
    }

    @Test
    fun shouldTriggerRecyclerScrollIfNeeded(){
        currentPhotoPositionRepository.currentPhotoPosition = 4
        viewModel.checkScrollNecessity(1,3)
        assertEquals(
            (viewModel.detailsViewAction.value as DetailsPropertyViewModel.DetailsViewAction.ScrollToPosition).position,4
        )
    }

    private var property1 = Property(
        "id1",
        "title1",
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

}