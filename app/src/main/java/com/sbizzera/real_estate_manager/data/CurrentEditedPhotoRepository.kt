package com.sbizzera.real_estate_manager.data

import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.PhotoOnEdit

class CurrentEditedPhotoRepository {

    val currentPhotoLD = MutableLiveData<PhotoOnEdit>()

    companion object {
        val instance: CurrentEditedPhotoRepository by lazy {
            CurrentEditedPhotoRepository()
        }
    }

}