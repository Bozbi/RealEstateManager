package com.sbizzera.real_estate_manager.utils

import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditUiState
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.PhotoOnEdit

fun List<Photo>.toPhotoInEditUiState(
    fileHelper: FileHelper,
    propertyId: String
): MutableList<PhotoOnEdit> {;
    val listToReturn = mutableListOf<PhotoOnEdit>()
    forEach {
        listToReturn.add(
            PhotoOnEdit(
                photoId = it.photoId,
                photoTitle = it.title,
                photoUri = fileHelper.getUriFromFileName(it.photoId, propertyId)
            )
        )
    }
    return listToReturn
}