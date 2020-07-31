package com.sbizzera.real_estate_manager.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.*
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.REMActivityViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.SynchroniseDataHelper
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment.MapViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment.MarkerUseCase
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer_fragment.PhotoViewerViewModel

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(REMActivityViewModel::class.java)) {
            return REMActivityViewModel(SynchroniseDataHelper(FirebaseStorageRepository.instance,
                FileHelper.instance,PropertyRepository.instance)) as T
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
                FileHelper.instance
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
                GeocodeResolver()
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
            return MapViewModel(App.instance, UserLocationRepo.newInstance(), MarkerUseCase(PropertyRepository.instance),
                CurrentPropertyIdRepository.instance, PropertyRepository.instance) as T
        }
        throw IllegalArgumentException("not such a viewModel Class")
    }
}