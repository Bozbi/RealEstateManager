package com.sbizzera.real_estate_manager.ui.rem_activity.main


import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.repository.SharedPreferencesRepo
import com.sbizzera.real_estate_manager.utils.helper.SynchroniseDataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class REMActivityViewModelTest {

    private lateinit var viewModel: REMActivityViewModel
    private lateinit var context: Context


    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        val synchroniseDataHelper = mock(SynchroniseDataHelper::class.java)
        val sharedPreferencesRepo = mock(SharedPreferencesRepo::class.java)
        val currentPropertyIdRepository = mock(CurrentPropertyIdRepository::class.java)

        viewModel = REMActivityViewModel(
            synchroniseDataHelper,
            sharedPreferencesRepo,
            currentPropertyIdRepository
        )
    }


    @Test
    fun onPhotoEditorAsked() {
        viewModel.onPhotoEditorAsked()
        assertEquals(REMActivityViewModel.ViewAction.LaunchPhotoEditor, viewModel.viewAction.value)
    }

    @Test
    fun onPropertyDetailsAsked() {
        viewModel.onPropertyDetailsAsked()
        assertEquals(REMActivityViewModel.ViewAction.LaunchDetails, viewModel.viewAction.value)
    }

    @Test
    fun onAddOrModifyPropertyAsked() {
        viewModel.onAddOrModifyPropertyAsked()
        assertEquals(REMActivityViewModel.ViewAction.LaunchEditProperty, viewModel.viewAction.value)
    }

    @Test
    fun onMapAsked() {
        viewModel.onMapAsked()
        assertEquals(REMActivityViewModel.ViewAction.LaunchMap, viewModel.viewAction.value)
    }

    @Test
    fun onPhotoViewerAsked() {
        val view = View(context)
        viewModel.onPhotoViewerAsked(view)
        assertTrue(viewModel.viewAction.value is REMActivityViewModel.ViewAction.LaunchPhotoViewer)
    }

}