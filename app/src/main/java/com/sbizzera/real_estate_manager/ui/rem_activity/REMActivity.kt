package com.sbizzera.real_estate_manager.ui.rem_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.EventListener
import com.sbizzera.real_estate_manager.ui.rem_activity.photoFragment.PhotoFragment
import com.sbizzera.real_estate_manager.utils.ViewModelFactory


class REMActivity : AppCompatActivity(),EventListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_e_m)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.container1,
                PhotoFragment.newInstance()
            ).commit()
        }

        val viewModel =
            ViewModelProvider(this, ViewModelFactory).get(REMActivityViewModel::class.java)


    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is PhotoFragment){
            fragment.listener = this
        }
    }

    override fun onLaunchCameraClick() {
        println("debug launch Camera Clicked")
    }

    override fun onLaunchGalleryClick() {
        println("debug launch Gallery Clicked")
    }
}
