package com.sbizzera.real_estate_manager.ui.rem_activity


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.REMActivityViewModel.ViewAction.*
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorFragment
import com.sbizzera.real_estate_manager.utils.ViewModelFactory


class REMActivity : AppCompatActivity(), OnUserAskTransactionEvent {

    private lateinit var viewModel: REMActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_r_e_m)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.container1,
                ListPropertyFragment.newInstance()
            ).commit()
        }

        viewModel = ViewModelProvider(this, ViewModelFactory).get(REMActivityViewModel::class.java)
        viewModel.viewAction.observe(this) { action ->
            when (action) {
                LaunchPhotoEditor -> {
                    supportFragmentManager.beginTransaction().add(R.id.container1, PhotoEditorFragment.newInstance())
                        .addToBackStack(null).commit()
                }
                LaunchDetails -> {
                    supportFragmentManager.beginTransaction().add(
                        R.id.container1,
                        DetailsPropertyFragment.newInstance()
                    ).addToBackStack(null).commit()
                }
                LaunchEditProperty -> {
                    supportFragmentManager.beginTransaction().add(
                        R.id.container1,
                        EditPropertyFragment.newInstance()
                    ).addToBackStack(null).commit()
                }
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnUserAskTransactionEventListenable) {
            fragment.setListener(this)
        }
    }


    override fun onPropertyDetailsAsked() {
        viewModel.onPropertyDetailsAsked()
    }

    override fun onAddPropertyAsked() {
        viewModel.onAddOrModifyPropertyAsked()
    }

    override fun onPhotoEditorAsked() {
        viewModel.onPhotoEditorAsked()
    }

    override fun onModifyPropertyAsked() {
        viewModel.onAddOrModifyPropertyAsked()
    }
}
