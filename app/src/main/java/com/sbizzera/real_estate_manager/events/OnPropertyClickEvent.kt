package com.sbizzera.real_estate_manager.events

interface OnPropertyClickEvent {
    fun onPropertyItemClick(position : Int)
    fun onAddPropertyClick(listener: OnPropertyChangeListener)
    fun onModifyPropertyClicked()
}