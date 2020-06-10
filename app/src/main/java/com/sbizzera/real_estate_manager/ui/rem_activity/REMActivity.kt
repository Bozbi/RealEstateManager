package com.sbizzera.real_estate_manager.ui.rem_activity


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnPhotoSelectedListener
import com.sbizzera.real_estate_manager.events.SelectPhotoSourceListener
import com.sbizzera.real_estate_manager.ui.rem_activity.photoFragment.PhotoFragment
import com.sbizzera.real_estate_manager.utils.ViewModelFactory


class REMActivity : AppCompatActivity(), SelectPhotoSourceListener {

    private lateinit var viewModel: REMActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_e_m)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.container1,
                PhotoFragment.newInstance()
            ).commit()
        }

        viewModel = ViewModelProvider(this, ViewModelFactory).get(REMActivityViewModel::class.java)
        viewModel.viewAction.observe(this){action->
            when(action){
                is REMActivityViewModel.ViewAction.OnPhotoSelected -> {
                    (supportFragmentManager.findFragmentById(R.id.container1) as? OnPhotoSelectedListener)?.let {
                        it.onPhotoSelected(action.photoUri)
                    }
                }
                is REMActivityViewModel.ViewAction.OnLaunchCameraClick -> {
                    startActivityForResult(action.intent,action.requestCode)
                }
                is REMActivityViewModel.ViewAction.OnLaunchGalleryClick -> {
                    startActivityForResult(action.intent,action.requestCode)
                }
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is PhotoFragment) {
            fragment.listener = this
        }
    }

    override fun onLaunchCameraClick() {
        viewModel.onLaunchCameraClick()
    }

    override fun onLaunchGalleryClick() {
        viewModel.onLaunchGalleryClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode,resultCode,data)

    }



}
