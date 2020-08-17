package com.sbizzera.real_estate_manager.utils.architecture_components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sbizzera.real_estate_manager.application.App
import com.sbizzera.real_estate_manager.data.repository.*
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property.DetailsPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.main.REMActivityViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property.EditPropertyViewModel
import com.sbizzera.real_estate_manager.data.repository.FilterRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.map.MapViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.map.MarkerUseCase
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer.PhotoViewerViewModel
import com.sbizzera.real_estate_manager.utils.CoroutineContextProvider
import com.sbizzera.real_estate_manager.utils.helper.FileHelper
import com.sbizzera.real_estate_manager.utils.helper.GeocodeResolver
import com.sbizzera.real_estate_manager.utils.helper.SynchroniseDataHelper
import org.threeten.bp.Clock

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(REMActivityViewModel::class.java)) {
            return REMActivityViewModel(
                SynchroniseDataHelper(
                    FirebaseStorageRepository.instance,
                    FileHelper.instance, PropertyRepository.instance
                ), SharedPreferencesRepo.instance,
                CurrentPropertyIdRepository.instance
            ) as T
        }
        if (modelClass.isAssignableFrom(DetailsPropertyViewModel::class.java)) {
            return DetailsPropertyViewModel(
                PropertyRepository.instance,
                CurrentPropertyIdRepository.instance,
                CurrentPhotoPositionRepo.instance,
                FileHelper.instance
            ) as T
        }
        if (modelClass.isAssignableFrom(ListPropertyViewModel::class.java)) {
            return ListPropertyViewModel(
                CurrentPropertyIdRepository.instance,
                PropertyRepository.instance,
                FileHelper.instance,
                FilterRepository.instance
            ) as T
        }
        if (modelClass.isAssignableFrom(EditPropertyViewModel::class.java)) {
            return EditPropertyViewModel(
                CurrentPropertyIdRepository.instance,
                PropertyInModificationRepository.instance,
                PropertyRepository.instance,
                FileHelper.instance,
                CurrentEditedPhotoRepository.instance,
                App.instance,
                GeocodeResolver(),
                SharedPreferencesRepo.instance,
                Clock.systemDefaultZone(),
                CoroutineContextProvider()
            ) as T
        }
        if (modelClass.isAssignableFrom(PhotoEditorViewModel::class.java)) {
            return PhotoEditorViewModel(
                CurrentEditedPhotoRepository.instance,
                PropertyInModificationRepository.instance
            ) as T
        }
        if(modelClass.isAssignableFrom(PhotoViewerViewModel::class.java)){
            return PhotoViewerViewModel(
                CurrentPropertyIdRepository.instance,
                PropertyRepository.instance,
                CurrentPhotoPositionRepo.instance,
                FileHelper.instance
            )as T
        }
        if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            return MapViewModel(
                App.instance, UserLocationRepo.newInstance(), MarkerUseCase(PropertyRepository.instance),
                CurrentPropertyIdRepository.instance, PropertyRepository.instance) as T
        }
        throw IllegalArgumentException("not such a viewModel Class")
    }
}