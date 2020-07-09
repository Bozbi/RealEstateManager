package com.sbizzera.real_estate_manager.events

import com.sbizzera.real_estate_manager.data.property.Property

interface OnPropertyClickEvent {
    fun onPropertyItemClick(propertyId:String)
    fun onAddPropertyClick()
    fun onModifyPropertyClicked()
}