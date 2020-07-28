package com.sbizzera.real_estate_manager.events

import android.view.View

interface OnPhotoActionListener {
    fun onPhotoClick(position : Int)
    fun onPhotoClickForTransition(position: Int, transitionView : View)
}