package com.sbizzera.real_estate_manager.data

class CurrentPhotoPositionRepo private constructor(){
    companion object{
        val instance by lazy { CurrentPhotoPositionRepo() }
    }

    var currentPhotoPosition = -1

}