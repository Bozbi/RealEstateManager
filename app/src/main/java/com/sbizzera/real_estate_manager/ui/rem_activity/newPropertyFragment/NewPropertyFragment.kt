package com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnPhotoSelectedListener
import com.sbizzera.real_estate_manager.events.SelectPhotoSourceListener
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.ViewAction.*
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_new_property.*
import kotlinx.android.synthetic.main.fragment_new_property.view.*

class NewPropertyFragment : Fragment(), OnPhotoSelectedListener {
    private lateinit var viewModel: NewPropertyFragmentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: NewPropertyPhotoRecyclerAdapter
    private lateinit var photoTitle: TextView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var addAPhotoFab: FloatingActionButton
    private lateinit var fromCameraFab: FloatingActionButton
    private lateinit var fromGalleryFab: FloatingActionButton
    private lateinit var editPhotoFab: FloatingActionButton
    private lateinit var imageBlur: FrameLayout
    lateinit var listener: SelectPhotoSourceListener

    companion object {
        fun newInstance() = NewPropertyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_property, container, false)
        photoTitle = view.photo_title
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory).get(NewPropertyFragmentViewModel::class.java)
        viewModel.uiModel.observe(this) { model: UiModel ->
            updateUi(model)
        }
        viewModel.viewAction.observe(this) { viewAction ->
            when (viewAction) {
                OpenPhotoMenu -> openPhotoMenu()
                ClosePhotoMenu -> closePhotoMenu()
                ShowPhotoTitle -> showPhotoTitle()
                HidePhotoTitle -> hidePhotoTitle()
                HideAddPhoto -> hideAddPhotoFab()
                ShowAddPhoto -> showAddPhotoFab()
                TakePhotoFromCamera -> listener.onLaunchCameraClick()
                TakePhotoFromGallery -> listener.onLaunchGalleryClick()
                is LaunchEditor -> listener.onPhotoEditorLaunch()
                HideEditPhoto -> hideEditPhoto()
                ShowEditPhoto -> showEditPhoto()
            }
        }

        recyclerViewAdapter = NewPropertyPhotoRecyclerAdapter()
        mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()

        recyclerView = view.property_photos_recycler_view.apply {
            layoutManager = mLayoutManager
            adapter = recyclerViewAdapter
        }

        snapHelper.attachToRecyclerView(view.property_photos_recycler_view)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                viewModel.onRecyclerScroll(newState, mLayoutManager.findFirstVisibleItemPosition())
            }
        })

        addAPhotoFab = view.add_a_photo_fab
        fromGalleryFab = view.from_gallery_fab
        fromCameraFab = view.from_camera_fab
        imageBlur = view.blur_img
        editPhotoFab = view.edit_photo_fab


        addAPhotoFab.setOnClickListener {
            viewModel.addPhotoClicked(fromCameraFab.alpha)
        }

        fromCameraFab.setOnClickListener {
            viewModel.takePhotoFromCameraClicked()
        }

        fromGalleryFab.setOnClickListener {
            viewModel.takePhotoFromGalleryClicked()
        }

        editPhotoFab.setOnClickListener {
            viewModel.editPhotoClicked(mLayoutManager.findFirstVisibleItemPosition())
        }

        return view

    }

    private fun showEditPhoto() {
        editPhotoFab.visibility = View.VISIBLE
        editPhotoFab.animate().apply {
            alpha(1f)
            duration = 300
        }
    }

    private fun hideEditPhoto() {
        editPhotoFab.animate().apply {
            alpha(0f)
            duration = 300
        }
        editPhotoFab.visibility = View.INVISIBLE
    }

    private fun showAddPhotoFab() {
        addAPhotoFab.visibility = View.VISIBLE
        addAPhotoFab.animate().apply {
            alpha(1f)
            duration = 300
        }
    }

    private fun hideAddPhotoFab() {
        addAPhotoFab.animate().apply {
            alpha(0f)
            duration = 300
        }
        addAPhotoFab.visibility = View.INVISIBLE
        addAPhotoFab.rotation = -45f
    }

    private fun hidePhotoTitle() {
        photoTitle.animate().apply {
            alpha(0f)
            duration = 300
        }
    }

    private fun showPhotoTitle() {
        photoTitle.animate().apply {
            alpha(0.6f)
            duration = 300
        }
    }

    private fun closePhotoMenu() {
        addAPhotoFab.animate().apply {
            rotation(0f)
        }
        fromCameraFab.animate().apply {
            alpha(0f)
        }
        fromGalleryFab.animate().apply {
            alpha(0f)
        }
        imageBlur.animate().apply {
            alpha(0f)
        }
    }

    private fun openPhotoMenu() {
        addAPhotoFab.animate().apply {
            rotation(45f)
        }
        fromCameraFab.animate().apply {
            alpha(1f)
        }
        fromGalleryFab.animate().apply {
            alpha(1f)
        }
        imageBlur.animate().apply {
            alpha(0.8f)
            duration = 100
        }
    }

    private fun updateUi(model: UiModel) {
        photo_title.text = model.currentPhotoTitle
        recyclerViewAdapter.listPhotos = model.photoList
        recyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onPhotoSelected(uri: String) {
        viewModel.onPhotoSelected(uri, mLayoutManager.findFirstVisibleItemPosition())
    }

}
