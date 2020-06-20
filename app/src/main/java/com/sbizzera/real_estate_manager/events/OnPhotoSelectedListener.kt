package com.sbizzera.real_estate_manager.events

import android.net.Uri
import com.sbizzera.real_estate_manager.data.photo.Photo

interface OnPhotoSelectedListener {
    fun onPhotoSelected(uri : String)
}