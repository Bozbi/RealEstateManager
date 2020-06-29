package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnPropertyChangeListener
import com.sbizzera.real_estate_manager.events.OnPropertyClick
import com.sbizzera.real_estate_manager.events.PropertyClickedListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragmentViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.utils.REFRESH_PROPERTIES_REQUEST_KEY
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_list_property.*
import kotlinx.android.synthetic.main.fragment_list_property.view.*
import kotlinx.android.synthetic.main.fragment_list_property.view.add_property_fab

class
ListPropertyFragment : Fragment(), PropertyClickedListenable,OnPropertyChangeListener {

    private lateinit var onPropertyClickListener: OnPropertyClick
    private lateinit var viewModel: ListPropertyFragmentViewModel

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
        val view = inflater.inflate(R.layout.fragment_list_property, container, false)

        val recyclerViewAdapter =
            ListPropertyFragmentAdapter()

        view.list_property_recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }

        recyclerViewAdapter.onPropertyClickListener = onPropertyClickListener

        viewModel = ViewModelProvider(this, ViewModelFactory)
            .get(ListPropertyFragmentViewModel::class.java)

        viewModel.uiModel.observe(viewLifecycleOwner) { model ->
            with(recyclerViewAdapter) {
                notifyDataSetChanged()
                list = model.properties
            }
        }
        viewModel.listPropertyViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                AddPropertyClicked -> onPropertyClickListener.addPropertyClick(this)
            }
        }

        view.add_property_fab.setOnClickListener {
            viewModel.addPropertyClicked()
        }

        return view
    }

    override fun setListener(listener: OnPropertyClick) {
        onPropertyClickListener = listener
    }

    override fun onPropertyChange() {
        viewModel.refreshProperties()
    }
}