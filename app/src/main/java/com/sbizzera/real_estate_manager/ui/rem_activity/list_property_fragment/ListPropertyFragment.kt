package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnPropertyChangeListener
import com.sbizzera.real_estate_manager.events.OnPropertyClickEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.PropertyFragmentsViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.PropertyFragmentsViewModel.ListPropertyViewAction.DetailsPropertyClicked
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_list_property.*


class ListPropertyFragment : Fragment(), OnPropertyClickEvent, OnUserAskTransactionEventListenable {

    private lateinit var viewModel: PropertyFragmentsViewModel
    private lateinit var onUserAskTransactionEvent: OnUserAskTransactionEvent

    companion object {
        fun newInstance(): ListPropertyFragment {
            return ListPropertyFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerViewAdapter = ListPropertyFragmentAdapter()
        recyclerViewAdapter.onPropertyClickListener = this

        list_property_recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
            var itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
            getDrawable(this.context, R.drawable.divider)?.let { itemDecoration.setDrawable(it) }
            addItemDecoration(itemDecoration)
        }


        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory)
            .get(PropertyFragmentsViewModel::class.java)

        viewModel.listUiState.observe(viewLifecycleOwner) { model ->
            with(recyclerViewAdapter) {
                list = model.listPropertyItems
                notifyDataSetChanged()
            }
        }
        viewModel.listViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                AddPropertyClicked -> {
                    onUserAskTransactionEvent.onAddPropertyAsked()
                }
                DetailsPropertyClicked -> {
                    onUserAskTransactionEvent.onPropertyDetailsAsked()
                }
            }
        }

        add_property_fab.setOnClickListener {
            viewModel.addPropertyClicked()
        }
        parentFragmentManager.setFragmentResultListener("REFRESH_PROPERTIES", viewLifecycleOwner) { _, _ ->
            viewModel.refreshProperties()
        }
    }

    override fun onPropertyItemClick(position: Int) {
        viewModel.onPropertyItemClick(position)
    }

    override fun onAddPropertyClick(listener: OnPropertyChangeListener) {

    }

    override fun onModifyPropertyClicked() {

    }

    override fun setListener(listener: OnUserAskTransactionEvent) {
        onUserAskTransactionEvent = listener
    }

}