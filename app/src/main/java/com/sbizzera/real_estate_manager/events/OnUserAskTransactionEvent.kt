package com.sbizzera.real_estate_manager.events

import android.net.Uri
import android.view.View

interface OnUserAskTransactionEvent {
    fun onPropertyDetailsAsked()
    fun onAddPropertyAsked()
    fun onModifyPropertyAsked()
    fun onPhotoEditorAsked()
    fun onPhotoViewerAsked(transitionView: View)
    fun onMapAsked()
    fun onCameraAsked(tempPhotoUri : String)
    fun onGalleryAsked()
}