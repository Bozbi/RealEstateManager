package com.sbizzera.real_estate_manager.events

interface OnPropertyClickEvent {
    fun onPropertyItemClick(propertyId:String)
    fun onAddPropertyClick()
    fun onModifyPropertyClicked()
}