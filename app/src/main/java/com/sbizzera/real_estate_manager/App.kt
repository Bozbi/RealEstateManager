package com.sbizzera.real_estate_manager

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        AndroidThreeTen.init(this)
    }

    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance
    }
}