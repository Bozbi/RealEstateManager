package com.sbizzera.real_estate_manager

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AndroidThreeTen.init(this)
    }

    companion object {
        private lateinit var INSTANCE: App
        val instance: App by lazy { INSTANCE }
    }
}