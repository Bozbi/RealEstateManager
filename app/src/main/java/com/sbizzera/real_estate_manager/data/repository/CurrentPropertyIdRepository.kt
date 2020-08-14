package com.sbizzera.real_estate_manager.data.repository

import androidx.lifecycle.MutableLiveData

class CurrentPropertyIdRepository private constructor(){

    val currentPropertyIdLiveData = MutableLiveData<String>()

    companion object{
        val instance : CurrentPropertyIdRepository by lazy { CurrentPropertyIdRepository() }
    }

}