package com.sbizzera.real_estate_manager

import androidx.multidex.MultiDexApplication
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.sbizzera.real_estate_manager.utils.SyncDataWorker
import java.util.concurrent.TimeUnit

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AndroidThreeTen.init(this)

        // Launching Periodic Sync Worker
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val work = PeriodicWorkRequestBuilder<SyncDataWorker>(1,TimeUnit.DAYS).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(work)
    }

    companion object {
        private lateinit var INSTANCE: App
        val instance: App by lazy { INSTANCE }
    }

}