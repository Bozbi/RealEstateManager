package com.openclassrooms.realestatemanager.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.ui.list_property_fragment.ListPropertyFragmentViewModel
import com.openclassrooms.realestatemanager.data.PropertyRepository
import com.openclassrooms.realestatemanager.ui.rem_activity.REMActivityViewModel
import java.lang.IllegalArgumentException

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(REMActivityViewModel::class.java)){
            return REMActivityViewModel(PropertyRepository) as T
        }
        if (modelClass.isAssignableFrom(ListPropertyFragmentViewModel::class.java)){
            return ListPropertyFragmentViewModel(PropertyRepository) as T
        }

        throw IllegalArgumentException("not such a viewModel Class")
    }
}