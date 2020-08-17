package com.sbizzera.real_estate_manager.data.repository

import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.PhotoOnEdit

class CurrentEditedPhotoRepository private constructor(){

    val currentPhotoLD = MutableLiveData<Pair<PhotoOnEdit,Int?>>()

    companion object {
        val instance: CurrentEditedPhotoRepository by lazy {
            CurrentEditedPhotoRepository()
        }
    }

}