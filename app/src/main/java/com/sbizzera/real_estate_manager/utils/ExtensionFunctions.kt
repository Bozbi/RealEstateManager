package com.sbizzera.real_estate_manager.utils

import com.sbizzera.real_estate_manager.data.photo.Photo

import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.PhotoOnEdit

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

