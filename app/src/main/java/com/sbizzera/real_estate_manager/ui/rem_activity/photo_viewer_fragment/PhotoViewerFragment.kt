package com.sbizzera.real_estate_manager.ui.rem_activity.photo_viewer_fragment

import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeClipBounds
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo_viewer.*

class PhotoViewerFragment : Fragment(), PhotoViewerRecyclerAdapter.OnViewHolderBound {

    private lateinit var viewModel: PhotoViewerViewModel
    private lateinit var photoViewerAdapter: PhotoViewerRecyclerAdapter

    companion object {
        fun newInstance() = PhotoViewerFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionSet().apply {
            addTransition(ChangeBounds())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addTransition(ChangeClipBounds())
                addTransition(ChangeTransform())
            }
            duration = 250
        }
        sharedElementReturnTransition = TransitionSet().apply {
            addTransition(ChangeBounds())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addTransition(ChangeClipBounds())
                addTransition(ChangeTransform())
            }
            duration = 250
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, ViewModelFactory).get(PhotoViewerViewModel::class.java)
        viewModel.photoViewerViewAction.observe(viewLifecycleOwner) { action ->
            when (action) {
                PhotoViewerViewModel.PhotoViewerViewAction.ViewHolderReady -> startPostponedEnterTransition()
            }
        }

        viewModel.photoList.observe(viewLifecycleOwner){photoList->
            updateUi(photoList)
        }

        photoViewerAdapter = PhotoViewerRecyclerAdapter()
        photoViewerAdapter.setListener(this)
        recycler_view.adapter = photoViewerAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)

        val photoViewerLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        photoViewerLayoutManager.scrollToPosition(viewModel.getCurrentPhotoPosition())

        recycler_view.layoutManager = photoViewerLayoutManager

        recycler_view.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    val currentPhotoPosition = snapHelper.findTargetSnapPosition(
                        photoViewerLayoutManager,
                        0,
                        0
                    )
                    viewModel.setCurrentPhotoPosition(currentPhotoPosition)
                }
            }
        })

        postponeEnterTransition()

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                recycler_view.findViewHolderForAdapterPosition(viewModel.getCurrentPhotoPosition())?.let {
                    sharedElements[names[0]] = it.itemView.findViewById(R.id.photo_img)
                }
            }
        })
    }

    private fun updateUi(photoList: List<PhotoInViewer>) {
        photoViewerAdapter.photoList = photoList
        photoViewerAdapter.notifyDataSetChanged()
    }

    override fun onViewHolderBound(position: Int) {
        viewModel.onViewHolderBound(position)
    }

}