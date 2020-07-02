package com.sbizzera.real_estate_manager.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sbizzera.real_estate_manager.App
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragmentViewModel
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.REMActivityViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyViewModel
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragmentViewModel
import java.lang.IllegalArgumentException

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(REMActivityViewModel::class.java)){
            return REMActivityViewModel(PropertyRepository,
                FileHelper,App.getInstance()) as T
        }
        if (modelClass.isAssignableFrom(ListPropertyFragmentViewModel::class.java)){
            return ListPropertyFragmentViewModel(
                PropertyRepository,
                FileHelper
            ) as T
        }
        if(modelClass.isAssignableFrom(EditPropertyFragmentViewModel::class.java)){
            return EditPropertyFragmentViewModel(PropertyRepository,FileHelper,App.getInstance()) as T
        }
        if(modelClass.isAssignableFrom(DetailsPropertyViewModel::class.java)){
            return DetailsPropertyViewModel() as T
        }


        throw IllegalArgumentException("not such a viewModel Class")
    }
}