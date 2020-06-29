package com.sbizzera.real_estate_manager.events

interface OnPropertyClick {
    fun onPropertyClick()
    fun addPropertyClick(listener: OnPropertyChangeListener)
}