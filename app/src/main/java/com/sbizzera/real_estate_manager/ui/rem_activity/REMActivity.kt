package com.sbizzera.real_estate_manager.ui.rem_activity


import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.REMActivityViewModel.ViewAction.*
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment.MapFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer_fragment.PhotoViewerFragment
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import com.sbizzera.real_estate_manager.utils.intent_contracts.AuthenticationContract
import kotlinx.android.synthetic.main.activity_r_e_m.*


class REMActivity : AppCompatActivity(), OnUserAskTransactionEvent {

    private lateinit var viewModel: REMActivityViewModel

    private lateinit var mMenu: Menu

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
                    supportFragmentManager.beginTransaction().replace(R.id.container1, PhotoEditorFragment.newInstance())
                        .addToBackStack(null).commit()
                }
                LaunchDetails -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container1,
                        DetailsPropertyFragment.newInstance()
                    ).addToBackStack(null).commit()
                }
                LaunchEditProperty -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container1,
                        EditPropertyFragment.newInstance()
                    ).addToBackStack(null).commit()
                }
                LaunchMap -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container1,
                        MapFragment.newInstance()
                    ).addToBackStack(null).commit()
                }
            }
        }

        setSupportActionBar(toolbar)
    }


    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnUserAskTransactionEventListenable) {
            fragment.setListener(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        mMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.synchronise -> {
                viewModel.syncLocalAndRemoteData()
            }
            R.id.disconnect -> {
                FirebaseAuth.getInstance().signOut()
            }
        }
        return true
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

    override fun onPhotoViewerAsked(transitionView: View) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addSharedElement(transitionView, transitionView.transitionName)
                }
            }
            .replace(
                R.id.container1,
                PhotoViewerFragment.newInstance(),
                null
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onMapAsked() {
        viewModel.onMapAsked()
    }

    override fun onModifyPropertyAsked() {
        viewModel.onAddOrModifyPropertyAsked()
    }

    private fun checkAuthenticatedUser() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            registerForActivityResult(AuthenticationContract()) {}.launch(null)
        }else{
            println("debug : Your're signed in")
        }
    }
}
