package com.sbizzera.real_estate_manager.utils.intent_contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.sbizzera.real_estate_manager.ui.auth_activity.AuthActivity

class AuthenticationContract : ActivityResultContract<String,Boolean> (){
    override fun createIntent(context: Context, input: String?): Intent {
//       return AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.EmailBuilder().build()))
//           .setIsSmartLockEnabled(false)
//            .build()

        return Intent(context,AuthActivity::class.java)

    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return when (resultCode) {
            Activity.RESULT_OK -> true
            else -> false
        }
    }
}