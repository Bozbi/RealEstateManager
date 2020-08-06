package com.sbizzera.real_estate_manager.ui.rem_activity


import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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

        viewModel = ViewModelProvider(this, ViewModelFactory).get(REMActivityViewModel::class.java)
        viewModel.checkPhoneConfiguration()
        viewModel.viewAction.observe(this) { action ->
//            when (action) {
//                is LaunchListFragment -> {
//                    supportFragmentManager.popBackStackImmediate(
//                        ListPropertyFragment::class.java.simpleName,
//                        FragmentManager.POP_BACK_STACK_INCLUSIVE
//                    )
//                    supportFragmentManager.beginTransaction()
//                        .replace(action.container.resource, ListPropertyFragment.newInstance())
//                        .addToBackStack(ListPropertyFragment::class.java.simpleName)
//                        .commit()
//                }
//                is LaunchPhotoEditor -> {
//                    supportFragmentManager.popBackStackImmediate(
//                        PhotoEditorFragment::class.java.simpleName,
//                        FragmentManager.POP_BACK_STACK_INCLUSIVE
//                    )
//                    supportFragmentManager.beginTransaction()
//                        .replace(action.container.resource, PhotoEditorFragment.newInstance())
//                        .addToBackStack(PhotoEditorFragment::class.java.simpleName).commit()
//                }
//                is LaunchDetails -> {
//                    supportFragmentManager.popBackStackImmediate(
//                        DetailsPropertyFragment::class.java.simpleName,
//                        FragmentManager.POP_BACK_STACK_INCLUSIVE
//                    )
//                    supportFragmentManager.beginTransaction().replace(
//                        action.container.resource,
//                        DetailsPropertyFragment.newInstance()
//                    ).addToBackStack(DetailsPropertyFragment::class.java.simpleName).commit()
//                }
//                is LaunchEditProperty -> {
//                    supportFragmentManager.popBackStackImmediate(
//                        EditPropertyFragment::class.java.simpleName,
//                        FragmentManager.POP_BACK_STACK_INCLUSIVE
//                    )
//                    supportFragmentManager.beginTransaction().replace(
//                        action.container.resource,
//                        EditPropertyFragment.newInstance()
//                    ).addToBackStack(EditPropertyFragment::class.java.simpleName).commit()
//                }
//                is LaunchMap -> {
//                    supportFragmentManager.popBackStackImmediate(
//                        MapFragment::class.java.simpleName,
//                        FragmentManager.POP_BACK_STACK_INCLUSIVE
//                    )
//                    supportFragmentManager
//                        .beginTransaction().replace(
//                        action.container.resource,
//                        MapFragment.newInstance()
//                    ).addToBackStack(MapFragment::class.java.simpleName).commit()
//                }
//            }
            when (action) {
                is LaunchListFragment -> {
                    supportFragmentManager.popBackStackImmediate(
                        ListPropertyFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.list_container, ListPropertyFragment.newInstance())
                        .addToBackStack(ListPropertyFragment::class.java.simpleName)
                        .commit()
                }
                is LaunchPhotoEditor -> {
                    supportFragmentManager.popBackStackImmediate(
                        PhotoEditorFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.details_container, PhotoEditorFragment.newInstance())
                        .addToBackStack(PhotoEditorFragment::class.java.simpleName).commit()
                }
                is LaunchDetails -> {
                    supportFragmentManager.popBackStackImmediate(
                        DetailsPropertyFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    supportFragmentManager.beginTransaction().replace(
                        R.id.details_container,
                        DetailsPropertyFragment.newInstance()
                    ).addToBackStack(DetailsPropertyFragment::class.java.simpleName).commit()
                }
                is LaunchEditProperty -> {
                    supportFragmentManager.popBackStackImmediate(
                        EditPropertyFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    supportFragmentManager.beginTransaction().replace(
                        R.id.details_container,
                        EditPropertyFragment.newInstance()
                    ).addToBackStack(EditPropertyFragment::class.java.simpleName).commit()
                }
                is LaunchMap -> {
                    supportFragmentManager.popBackStackImmediate(
                        MapFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    supportFragmentManager
                        .beginTransaction().replace(
                            R.id.details_container,
                            MapFragment.newInstance()
                        ).addToBackStack(MapFragment::class.java.simpleName).commit()
                }
            }
        }

        if (savedInstanceState == null) {
            viewModel.launchListFragment()
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
        //TODO Pass throught Viewmodel
        supportFragmentManager.popBackStackImmediate(
            PhotoViewerFragment::class.java.simpleName,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addSharedElement(transitionView, transitionView.transitionName)
                }
            }
            .replace(
                R.id.details_container,
                PhotoViewerFragment.newInstance(),
                null
            )
            .addToBackStack(PhotoViewerFragment::class.java.simpleName)
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
        } else {
            println("debug : Your're signed in")
        }
    }

}
