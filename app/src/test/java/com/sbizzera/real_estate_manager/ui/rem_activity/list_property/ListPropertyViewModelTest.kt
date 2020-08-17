package com.sbizzera.real_estate_manager.ui.rem_activity.list_property

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.model.Photo
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.repository.FilterRepository
import com.sbizzera.real_estate_manager.data.repository.PropertyRepository
import com.sbizzera.real_estate_manager.test_utils.getOrAwaitValue
import com.sbizzera.real_estate_manager.utils.CUSTOM_DATE_FORMATTER
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

@RunWith(MockitoJUnitRunner::class)
class ListPropertyViewModelTest {

    private lateinit var viewModel: ListPropertyViewModel
    private lateinit var filterRepository: FilterRepository
    private lateinit var propertyRepo: PropertyRepository
    private lateinit var currentPropertyIdRepository: CurrentPropertyIdRepository
    private lateinit var fileHelper: FileHelper

    private val mockedPropertiesLD = MutableLiveData<List<Property>>()
    private val currentPropertyIdLD = MutableLiveData<String>()

    @Mock
    private lateinit var context: Context

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        currentPropertyIdRepository = mock(CurrentPropertyIdRepository::class.java)
        propertyRepo = mock(PropertyRepository::class.java)
        fileHelper = mock(FileHelper::class.java)
        filterRepository = FilterRepository.instance
        context = mock(Context::class.java)

        doReturn(mockedPropertiesLD).`when`(propertyRepo).getAllLocalProperties()
        doReturn(currentPropertyIdLD).`when`(currentPropertyIdRepository).currentPropertyIdLiveData

        `when`(fileHelper.getUriFromFileName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenAnswer { invocation ->
                "${invocation.arguments[0]}/${invocation.arguments[1]}.mock"
            }


        viewModel = ListPropertyViewModel(
            currentPropertyIdRepository,
            propertyRepo,
            fileHelper,
            filterRepository
        )
    }

    @Test
    fun addPropertyClicked() {
        viewModel.addPropertyClicked()
        assertEquals(ListPropertyViewModel.ListPropertyViewAction.AddPropertyClicked, viewModel.listViewAction.value)
    }

    @Test
    fun onPropertyItemClick() {
        viewModel.onPropertyItemClick("test")
        assertEquals(ListPropertyViewModel.ListPropertyViewAction.DetailsPropertyClicked, viewModel.listViewAction.value)
    }

    @Test
    fun shouldReturnProperUiState() {
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        val model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id2",
                        "title2",
                        "photo1inProperty2/id2.mock",
                        "$200",
                        "Mansion",
                        android.R.color.white
                    ),
                    ListPropertyItemUiState(
                        "id1",
                        "title1",
                        "photo1inProperty1/id1.mock",
                        "$100",
                        "House",
                        R.color.colorAccent
                    )
                )
            ), model
        )
    }

    @Test
    fun shouldReturnNullIfNoPropertiesInRepo() {
        viewModel.resetFilters()
        mockedPropertiesLD.value = null
        currentPropertyIdLD.value = "id1"

        val model = viewModel.listUiStateLD.getOrAwaitValue()

        assertNull(model)
    }

    @Test
    fun shouldFilterProperlyIfPriceNotInRange() {
        viewModel.onPriceRangeFilterChange(150f, 200f)
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        val model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id2",
                        "title2",
                        "photo1inProperty2/id2.mock",
                        "$200",
                        "Mansion",
                        android.R.color.white
                    )
                )
            ), model
        )
    }

    @Test
    fun shouldFilterProperlyIfPoiNotInProperty() {
        viewModel.resetFilters()
        viewModel.onChipCheckedChange(PointOfInterest.SHOPS.name, true)
        viewModel.onChipCheckedChange(PointOfInterest.SCHOOLS.name, true)
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        val model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id2",
                        "title2",
                        "photo1inProperty2/id2.mock",
                        "$200",
                        "Mansion",
                        android.R.color.white
                    )
                )
            ), model
        )
    }

    @Test
    fun shouldFilterProperlyIfPropertySoldDateEmpty() {
        viewModel.onSoldDateChange(0, 10)
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        val model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(ListUiState(listOf()), model)
    }

    @Test
    fun shouldFilterProperlyIfSoldDateInRange() {
        val startDate = LocalDate.parse("01/01/2020", CUSTOM_DATE_FORMATTER)
        val endDate = LocalDate.parse("02/01/2020", CUSTOM_DATE_FORMATTER)

        val startDateInEpochMillis = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endDateInEpochMillis = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

        viewModel.resetFilters()
        viewModel.onSoldDateChange(startDateInEpochMillis, endDateInEpochMillis)
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        var model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id2",
                        "title2",
                        "photo1inProperty2/id2.mock",
                        "$200",
                        "Mansion",
                        android.R.color.white
                    )
                )
            ), model
        )

        viewModel.onSoldSinceDateCleared()

        model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id2",
                        "title2",
                        "photo1inProperty2/id2.mock",
                        "$200",
                        "Mansion",
                        android.R.color.white
                    ),
                    ListPropertyItemUiState(
                        "id1",
                        "title1",
                        "photo1inProperty1/id1.mock",
                        "$100",
                        "House",
                        R.color.colorAccent
                    )
                )
            ), model
        )
    }

    @Test
    fun shouldFilterProperlyIfAvailabilityDateInRangeThanClear() {
        val startDate = LocalDateTime.parse("2020-01-01T00:00:00.000", DateTimeFormatter.ISO_DATE_TIME)
        val endDate = LocalDateTime.parse("2020-01-01T23:59:59.999", DateTimeFormatter.ISO_DATE_TIME)

        val startDateInEpochMillis = startDate.toInstant(ZoneOffset.UTC).toEpochMilli()
        val endDateInEpochMillis = endDate.toInstant(ZoneOffset.UTC).toEpochMilli()

        viewModel.resetFilters()
        viewModel.onCreationDateRangeChange(startDateInEpochMillis, endDateInEpochMillis)
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        var model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id1",
                        "title1",
                        "photo1inProperty1/id1.mock",
                        "$100",
                        "House",
                        R.color.colorAccent
                    )
                )
            ), model
        )

        viewModel.onAvailableDateCleared()

        model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id2",
                        "title2",
                        "photo1inProperty2/id2.mock",
                        "$200",
                        "Mansion",
                        android.R.color.white
                    ),
                    ListPropertyItemUiState(
                        "id1",
                        "title1",
                        "photo1inProperty1/id1.mock",
                        "$100",
                        "House",
                        R.color.colorAccent
                    )
                )
            ), model
        )


    }

    @Test
    fun shouldFilterProperlyIfSurfaceInSurfaceRange() {

        viewModel.resetFilters()
        viewModel.onSurfaceRangeFilterChange(19f, 21f)
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        val model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id2",
                        "title2",
                        "photo1inProperty2/id2.mock",
                        "$200",
                        "Mansion",
                        android.R.color.white
                    )
                )
            ), model
        )
    }

    @Test
    fun shouldFilterProperlyIfRoomCountInRoomRange() {
        viewModel.resetFilters()
        viewModel.onRoomRangeFilterChange(1f, 12f)
        mockedPropertiesLD.value = listOf(property1, property2)
        currentPropertyIdLD.value = "id1"

        val model = viewModel.listUiStateLD.getOrAwaitValue()

        assertEquals(
            ListUiState(
                listOf(
                    ListPropertyItemUiState(
                        "id1",
                        "title1",
                        "photo1inProperty1/id1.mock",
                        "$100",
                        "House",
                        R.color.colorAccent
                    )
                )
            ), model
        )


    }

    @Test
    fun onAvailableSinceDateClick() {
        viewModel.onAvailableSinceDateClick()
        assertEquals(ListPropertyViewModel.FilterDialogViewAction.CreationDateRangeClicked, viewModel.filterViewAction.value)
    }

    @Test
    fun onSoldBetwennDateClick() {
        viewModel.onSoldSinceDateClick()
        assertEquals(ListPropertyViewModel.FilterDialogViewAction.SoldDateRangeClicked, viewModel.filterViewAction.value)
    }

    @Test
    fun shouldReturnProperFilterUiState() {
        val poiMap = mutableMapOf<PointOfInterest, Boolean>()
        PointOfInterest.values().forEach {
            poiMap[it] = false
        }

        viewModel.resetFilters()

        var model = viewModel.filterUiState.getOrAwaitValue()
        assertEquals(
            FilterUiState(
                0f, 3000000f, 0f, 200f, 0f, 10f,
                poiMap,
                "",
                ""
            ), model
        )

        viewModel.onCreationDateRangeChange(20000000,459872349)
        viewModel.onSoldDateChange(0,100000000)

        model = viewModel.filterUiState.getOrAwaitValue()
        assertEquals(
            FilterUiState(
                0f, 3000000f, 0f, 200f, 0f, 10f,
                poiMap,
                "01/01/1970 and 06/01/1970",
                "01/01/1970 and 02/01/1970"
            ), model
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

    private var property2 =
        Property(
            "id2",
            "title2",
            "Mansion",
            200,
            listOf(
                Photo("photo1inProperty2", "photo1inProperty2Title"),
                Photo("photo2inProperty2", "photo2inProperty2Title")
            ),
            "23/11/2022T12:17",
            "Property2Description",
            "address2",
            "cityCode2",
            "cityName2",
            20,
            21,
            22,
            23,
            listOf(
                PointOfInterest.SHOPS,
                PointOfInterest.SCHOOLS
            ),
            "01/01/2020",
            "2020-01-02T00:00:00.000",
            4.0,
            5.0,
            "Toto"
        )

}


