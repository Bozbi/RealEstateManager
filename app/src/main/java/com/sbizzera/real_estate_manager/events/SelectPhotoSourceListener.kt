package com.sbizzera.real_estate_manager.events

import com.sbizzera.real_estate_manager.data.photo.Photo

interface SelectPhotoSourceListener {
    fun onLaunchCameraClick()
    fun onLaunchGalleryClick()
    fun onPhotoEditorLaunch()
}