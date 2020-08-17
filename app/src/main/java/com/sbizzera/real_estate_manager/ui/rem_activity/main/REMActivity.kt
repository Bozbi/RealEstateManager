package com.sbizzera.real_estate_manager.ui.rem_activity.main


import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.dialog.ChooseUserMaterialDialog
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.OnPropertySavedListener
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.main.REMActivityViewModel.ViewAction.*
import com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment.MapFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_editor.PhotoEditorFragment
import com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer_fragment.PhotoViewerFragment
import com.sbizzera.real_estate_manager.utils.architecture_components.ViewModelFactory
import kotlinx.android.synthetic.main.activity_r_e_m.*


class REMActivity : AppCompatActivity(), OnUserAskTransactionEvent, OnPropertySavedListener {

    private val CAMERA_INTENT_REQUEST_CODE = 123
    private val GALLERY_INTENT_REQUEST_CODE = 124

    private lateinit var viewModel: REMActivityViewModel
    private lateinit var mMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_e_m)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory
        ).get(REMActivityViewModel::class.java)
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
                        EditPropertyFragment.newInstance(),
                        EditPropertyFragment::class.java.simpleName
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
                    val dialog =
                        ChooseUserMaterialDialog()
                    dialog.show(supportFragmentManager, null)
                }
                HideBackButton -> {
                    supportActionBar?.let {
                        it.setDisplayShowHomeEnabled(false)
                        it.setDisplayHomeAsUpEnabled(false)
                    }
                }
                LaunchSync -> {
                    sync_background.visibility = View.VISIBLE
                    sync_progress.visibility = View.VISIBLE
                }
                SyncEnd -> {
                    sync_background.visibility = View.GONE
                    sync_progress.visibility = View.GONE
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
                viewModel.logOut()
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
        Snackbar.make(
            contextView,
            getString(R.string.insertion_completed_message),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkBackIconDisplay()
    }

    private fun checkBackIconDisplay() {
        val backStackList = mutableListOf<String>()
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            backStackList.add(supportFragmentManager.getBackStackEntryAt(i).name!!)
        }
        viewModel.shouldDisplayBackIconAndClearCurrentPropertyRepo(backStackList)
    }

    override fun onCameraAsked(tempPhotoUri: String) {
        val takePictureIntent2 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent2.resolveActivity(packageManager)
        takePictureIntent2.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            val tempPhotoUriInUri = Uri.parse(tempPhotoUri)
            takePictureIntent2.clipData = ClipData.newRawUri("", tempPhotoUriInUri)
            takePictureIntent2.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        @Suppress("DEPRECATION")
        startActivityForResult(takePictureIntent2, CAMERA_INTENT_REQUEST_CODE)
    }

    override fun onGalleryAsked() {
        @Suppress("DEPRECATION")
        startActivityForResult(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            GALLERY_INTENT_REQUEST_CODE
        )
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == CAMERA_INTENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                (supportFragmentManager.fragments.filter { it.tag == EditPropertyFragment::class.java.simpleName }[0] as EditPropertyFragment).onResultFromCamera()
            }
        }
        if (requestCode == GALLERY_INTENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                (supportFragmentManager.fragments.filter { it.tag == EditPropertyFragment::class.java.simpleName }[0] as EditPropertyFragment).onResultFromGallery(
                    intent?.data
                )
            }
        }
    }

}
