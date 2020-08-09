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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.REMActivityViewModel.ViewAction.*
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.OnPropertySavedListener
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment.MapFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer_fragment.PhotoViewerFragment
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.activity_r_e_m.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class REMActivity : AppCompatActivity(), OnUserAskTransactionEvent, OnPropertySavedListener {

    private lateinit var viewModel: REMActivityViewModel
    private lateinit var mMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_e_m)

        viewModel = ViewModelProvider(this, ViewModelFactory).get(REMActivityViewModel::class.java)
        viewModel.viewAction.observe(this) { action ->
            when (action) {

                LaunchPhotoEditor -> {
                    supportFragmentManager.popBackStackImmediate(
                        PhotoEditorFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.details_container, PhotoEditorFragment.newInstance())
                        .addToBackStack(PhotoEditorFragment::class.java.simpleName).commit()
                }
                LaunchDetails -> {
                    supportActionBar?.let {
                        it.setDisplayHomeAsUpEnabled(true)
                        it.setDisplayShowHomeEnabled(true)
                    }
                    supportFragmentManager.popBackStackImmediate(
                        DetailsPropertyFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    supportFragmentManager.beginTransaction().replace(
                        R.id.details_container,
                        DetailsPropertyFragment.newInstance(),
                        DetailsPropertyFragment::class.java.simpleName
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
                LaunchMap -> {
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
                is LaunchPhotoViewer -> {

                    supportFragmentManager.popBackStackImmediate(
                        PhotoViewerFragment::class.java.simpleName,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )

                    supportFragmentManager
                        .beginTransaction()
                        .setReorderingAllowed(true).apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                addSharedElement(action.transitionView, action.transitionView.transitionName)
                            }
                        }
                        .replace(
                            R.id.details_container,
                            PhotoViewerFragment.newInstance()
                        )
                        .addToBackStack(PhotoViewerFragment::class.java.simpleName)
                        .commit()
                }
                ShowChooseUserDialog -> {
                    val dialog = ChooseUserMaterialDialog()
                    dialog.setListener(object : ChooseUserMaterialDialog.DialogDismissListener {
                        override fun onDialogDismiss(userName: String) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.setUserNameInSharedPrefs(userName)
                                withContext(Dispatchers.Main) {
                                    viewModel.checkUserIsLogged()
                                }
                            }
                        }
                    })
                    dialog.show(supportFragmentManager, null)
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.popBackStackImmediate(
                ListPropertyFragment::class.java.simpleName,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_container, ListPropertyFragment.newInstance())
                .commit()

        }
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        viewModel.checkUserIsLogged()
    }




    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is OnUserAskTransactionEventListenable) {
            fragment.setListener(this)
        }
        if (fragment is EditPropertyFragment) {
            fragment.setOnPropertySavedListener(this)
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
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.logOut()
                    withContext(Dispatchers.Main) {
                        viewModel.checkUserIsLogged()
                    }

                }
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
        viewModel.onPhotoViewerAsked(transitionView)
    }

    override fun onMapAsked() {
        viewModel.onMapAsked()
    }

    override fun onModifyPropertyAsked() {
        viewModel.onAddOrModifyPropertyAsked()
    }

    override fun onPropertySaved() {
        val contextView = findViewById<View>(R.id.details_container)
        Snackbar.make(contextView, "Property has been saved", Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkBackIconDisplay()
    }

    private fun checkBackIconDisplay() {
        //TODO pass this threw viewModel check new insertion
        val backStackList = mutableListOf<String>()
        for(i in 0 until supportFragmentManager.backStackEntryCount){
            backStackList.add(supportFragmentManager.getBackStackEntryAt(i).name!!)
        }
        if(!backStackList.contains(DetailsPropertyFragment::class.java.simpleName)){
            viewModel.clearCurrentPropertyId()
            supportActionBar?.let {
                it.setDisplayShowHomeEnabled(false)
                it.setDisplayHomeAsUpEnabled(false)
            }
        }
    }


}
