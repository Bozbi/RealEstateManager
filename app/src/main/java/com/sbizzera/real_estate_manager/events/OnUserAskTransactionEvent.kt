package com.sbizzera.real_estate_manager.events

interface OnUserAskTransactionEvent {
    fun onPropertyDetailsAsked()
    fun onAddPropertyAsked()
    fun onModifyPropertyAsked()
    fun onLaunchCameraAsked()
    fun onLaunchGalleryAsked()
    fun onPhotoEditorAsked()
}