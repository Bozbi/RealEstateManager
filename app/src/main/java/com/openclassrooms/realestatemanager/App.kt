package com.openclassrooms.realestatemanager

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object{
        private lateinit var instance : App
        fun getInstance():App = instance
    }
}