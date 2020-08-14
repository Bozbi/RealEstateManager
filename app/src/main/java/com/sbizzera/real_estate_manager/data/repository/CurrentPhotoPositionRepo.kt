package com.sbizzera.real_estate_manager.data.repository

class CurrentPhotoPositionRepo private constructor(){
    companion object{
        val instance by lazy { CurrentPhotoPositionRepo() }
    }

    var currentPhotoPosition = -1

}