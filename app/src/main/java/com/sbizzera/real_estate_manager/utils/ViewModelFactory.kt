package com.sbizzera.real_estate_manager.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.data.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.data.PropertyInModificationRepository
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.data.CurrentEditedPhotoRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.REMActivityViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorViewModel

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(REMActivityViewModel::class.java)) {
            return REMActivityViewModel() as T
        }
        if (modelClass.isAssignableFrom(DetailsPropertyViewModel::class.java)) {
            return DetailsPropertyViewModel(
                PropertyRepository.instance,
                CurrentPropertyIdRepository.instance,
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
                App.instance
            ) as T
        }
        if (modelClass.isAssignableFrom(PhotoEditorViewModel::class.java)) {
            return PhotoEditorViewModel(
                CurrentEditedPhotoRepository.instance,
                PropertyInModificationRepository.instance
            ) as T
        }
        throw IllegalArgumentException("not such a viewModel Class")
    }
}