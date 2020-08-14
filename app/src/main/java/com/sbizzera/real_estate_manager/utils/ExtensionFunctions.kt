package com.sbizzera.real_estate_manager.utils

import com.sbizzera.real_estate_manager.data.model.Photo

import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.PhotoOnEdit
import com.sbizzera.real_estate_manager.utils.helper.FileHelper

fun List<Photo>.toPhotoInEditUiState(
    fileHelper: FileHelper,
    propertyId: String
): MutableList<PhotoOnEdit> {
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

