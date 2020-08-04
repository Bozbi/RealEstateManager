package com.sbizzera.real_estate_manager.ui.auth_activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.sbizzera.real_estate_manager.R
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        login_btn.setOnClickListener {
            lifecycleScope.launch {
                val task = FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(username_edt.text.toString(), password_edt.text.toString()).addOnCompleteListener() {task ->
                        if(task.isSuccessful){
                            println("debug : login success")
                        }else{
                            println("debug :login failed")
                        }

                    }

            }
        }
    }

}