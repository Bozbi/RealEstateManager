package com.sbizzera.real_estate_manager.ui.rem_activity.details_property

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property.DetailsPropertyViewModel.DetailsViewAction.ModifyPropertyClicked
import com.sbizzera.real_estate_manager.utils.architecture_components.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details_property.*

class DetailsPropertyFragment : Fragment(), OnUserAskTransactionEventListenable,
    DetailsPropertyPhotoAdapter.OnPhotoClickForTransitionListener, DetailsPropertyPhotoAdapter.OnViewHolderBoundListener {

    companion object {
        fun newInstance() = DetailsPropertyFragment()
    }

    private lateinit var viewModelDetails: DetailsPropertyViewModel
    private val recyclerAdapter = DetailsPropertyPhotoAdapter()
    private lateinit var onUserAskTransactionEvent: OnUserAskTransactionEvent
    private lateinit var detailsLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.details_to_photo_viewer_exit)
        returnTransition = TransitionInflater.from(context).inflateTransition(R.transition.details_to_photo_viewer_exit)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModelDetails = ViewModelProvider(
            this,
            ViewModelFactory
        ).get(DetailsPropertyViewModel::class.java)
        initLayoutManager()
        initRecyclerView()
        initClickListeners()
        prepareAnimation()
        initObservers()
    }

    private fun initClickListeners() {
        modify_btn.setOnClickListener {
            viewModelDetails.modifyPropertyClicked()
        }

        map_img.setOnClickListener {
            onUserAskTransactionEvent.onMapAsked()
        }
    }

    private fun prepareAnimation() {
        postponeEnterTransition()

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                recycler_view.findViewHolderForAdapterPosition(viewModelDetails.getCurrentPhotoPosition())?.let {
                    sharedElements[names[0]] = it.itemView.findViewById(R.id.photo_in_details_img)
                }
            }
        })
    }

    private fun initObservers() {
        viewModelDetails.detailsUiStateLD.observe(viewLifecycleOwner) { model ->
            updateUi(model)
            detailsLayoutManager.scrollToPosition(viewModelDetails.getCurrentPhotoPosition())
        }
        viewModelDetails.detailsViewAction.observe(viewLifecycleOwner) { action ->
            when (action) {
                ModifyPropertyClicked -> {
                    onUserAskTransactionEvent.onModifyPropertyAsked()
                }
                DetailsPropertyViewModel.DetailsViewAction.ViewHolderReady -> startPostponedEnterTransition()
                is DetailsPropertyViewModel.DetailsViewAction.ScrollToPosition -> detailsLayoutManager.scrollToPosition(
                    action.position
                )
            }
        }
    }

    private fun initRecyclerView() {
        recyclerAdapter.setListener(this, this)
        recycler_view.adapter = recyclerAdapter
        recycler_view.isNestedScrollingEnabled = false

        recycler_view.apply {
            layoutManager = detailsLayoutManager
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }
    }

    private fun initLayoutManager() {
        detailsLayoutManager = object : LinearLayoutManager(requireContext(), HORIZONTAL, false) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                viewModelDetails.checkScrollNecessity(
                    detailsLayoutManager.findFirstCompletelyVisibleItemPosition(),
                    detailsLayoutManager.findLastCompletelyVisibleItemPosition()

                )
            }

            override fun canScrollVertically(): Boolean {
                return false
            }
        }
    }


    private fun updateUi(model: DetailsUiState) {
        title_txt.text = model.title
        type_txt.text = model.type
        price_txt.text = model.price
        availability_txt.text = model.availableOrSoldSinceText
        recyclerAdapter.setList(model.listPropertyPhoto)
        recyclerAdapter.notifyDataSetChanged()
        surface_txt.text = model.surface
        room_txt.text = model.roomsCount
        bedroom_txt.text = model.bedRoomsCount
        bathroom_txt.text = model.bathroomsCount
        description_txt.text = model.description
        address_txt.text = model.addressText
        poi_txt.text = model.poiText
        Picasso.get().load(model.staticMapUri).into(map_img)
        added_by_txt.text = model.agentText
    }


    override fun onPhotoClickedForTransition(position: Int, transitionView: View) {
        viewModelDetails.photoClicked(position)
        onUserAskTransactionEvent.onPhotoViewerAsked(transitionView)
    }

    override fun setListener(listener: OnUserAskTransactionEvent) {
        onUserAskTransactionEvent = listener
    }

    override fun onViewHolderBound(position: Int) {
        viewModelDetails.onViewHolderBound(position)
    }
}
