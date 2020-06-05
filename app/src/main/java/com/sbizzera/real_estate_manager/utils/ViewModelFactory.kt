package com.sbizzera.real_estate_manager.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragmentViewModel
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.data.property_register.PropertyRegisterRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.REMActivityViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.photoFragment.PhotoFragmentViewModel
import java.lang.IllegalArgumentException

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(REMActivityViewModel::class.java)){
            return REMActivityViewModel(PropertyRepository,PropertyRegisterRepository) as T
        }
        if (modelClass.isAssignableFrom(ListPropertyFragmentViewModel::class.java)){
            return ListPropertyFragmentViewModel(
                PropertyRepository
            ) as T
        }
        if (modelClass.isAssignableFrom(PhotoFragmentViewModel::class.java)){
            return PhotoFragmentViewModel() as T
        }


        throw IllegalArgumentException("not such a viewModel Class")
    }
}