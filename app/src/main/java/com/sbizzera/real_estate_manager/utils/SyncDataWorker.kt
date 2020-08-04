package com.sbizzera.real_estate_manager.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sbizzera.real_estate_manager.data.FirebaseStorageRepository
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.SynchroniseDataHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncDataWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val synchroniseDataHelper = SynchroniseDataHelper(
            FirebaseStorageRepository.instance,
            FileHelper.instance,
            PropertyRepository.instance
        )
        CoroutineScope(Dispatchers.IO).launch {
            synchroniseDataHelper.synchroniseData()
        }
        return Result.success()
    }
}