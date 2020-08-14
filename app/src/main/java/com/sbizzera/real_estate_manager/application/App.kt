package com.sbizzera.real_estate_manager.application

import androidx.multidex.MultiDexApplication
import androidx.work.*
import com.jakewharton.threetenabp.AndroidThreeTen
import com.sbizzera.real_estate_manager.utils.worker.SyncDataWorker
import java.util.concurrent.TimeUnit

class
App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AndroidThreeTen.init(this)
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val work = PeriodicWorkRequestBuilder<SyncDataWorker>(1, TimeUnit.DAYS).addTag(
            SyncDataWorker::class.java.simpleName).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(this)
        val workInfoList = workManager.getWorkInfos(
            WorkQuery.Builder.fromTags(mutableListOf(SyncDataWorker::class.java.simpleName)).build()
        ).get()
        if (workInfoList.none { it.tags.contains(SyncDataWorker::class.java.simpleName) }) {
            workManager.enqueueUniquePeriodicWork(
                SyncDataWorker::class.java.simpleName,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
        }
    }

    companion object {
        private lateinit var INSTANCE: App
        val instance: App by lazy { INSTANCE }
    }

}
