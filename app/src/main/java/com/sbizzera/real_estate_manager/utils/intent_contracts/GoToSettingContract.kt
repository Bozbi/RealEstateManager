package com.sbizzera.real_estate_manager.utils.intent_contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class GoToSettingContract() : ActivityResultContract<String, Boolean>() {
    override fun createIntent(context: Context, input: String?): Intent {
        val uri = Uri.fromParts("package", context.packageName, null)

        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = uri
            resolveActivity(context.packageManager)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return when (resultCode) {
            Activity.RESULT_OK -> true
            else -> false
        }
    }
}