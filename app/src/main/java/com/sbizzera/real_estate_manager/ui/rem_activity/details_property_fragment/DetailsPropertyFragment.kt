package com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyViewModel.DetailsViewAction.ModifyPropertyClicked
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.details_property_fragment.*

class DetailsPropertyFragment : Fragment(),OnUserAskTransactionEventListenable{


    companion object {
        fun newInstance() = DetailsPropertyFragment()
    }

    private lateinit var viewModelDetails: DetailsPropertyViewModel
    private val recyclerAdapter = DetailsPropertyPhotoAdapter()
    private lateinit var onUserAskTransactionEvent: OnUserAskTransactionEvent


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.details_property_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModelDetails = ViewModelProvider(requireActivity(), ViewModelFactory).get(DetailsPropertyViewModel::class.java)
        recycler_view.adapter = recyclerAdapter
        recycler_view.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModelDetails.detailsUiStateLD.observe(viewLifecycleOwner) { model ->
            updateUi(model)
        }
        viewModelDetails.detailsViewAction.observe(viewLifecycleOwner) { action ->
            when (action) {
                ModifyPropertyClicked -> {
                    onUserAskTransactionEvent.onModifyPropertyAsked()
                }
            }
        }
        modify_txt.setOnClickListener {
            viewModelDetails.modifyPropertyClicked()
        }
    }

    private fun updateUi(model: DetailsUiState) {
        title_txt.text = model.title
        type_txt.text = model.type
        price_txt.text = model.price
        availability_txt.text = model.availableOrSoldSinceText
        recyclerAdapter.photoList = model.listPropertyPhoto
        recyclerAdapter.notifyDataSetChanged()
        surface_txt.text = model.surface
        room_txt.text = model.roomsCount
        bedroom_txt.text = model.bedRoomsCount
        bathroom_txt.text = model.bathroomsCount
        description_txt.text = model.description
        address_txt.text = model.addressText
        poi_txt.text = model.poiText
        Glide.with(map_img).load(model.staticMapUri).into(map_img)
    }


    override fun setListener(listener: OnUserAskTransactionEvent) {
            onUserAskTransactionEvent = listener
    }
   // TODO Create BasePropertyFragment for avoiding empty functions

}
