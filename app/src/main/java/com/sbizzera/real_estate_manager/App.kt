package com.sbizzera.real_estate_manager

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        AndroidThreeTen.init(this)
    }

    companion object{
        private lateinit var instance : App
        fun getInstance():App = instance
    }
}