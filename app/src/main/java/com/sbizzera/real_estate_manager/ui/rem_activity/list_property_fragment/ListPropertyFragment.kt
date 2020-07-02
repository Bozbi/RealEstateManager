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
import com.sbizzera.real_estate_manager.events.OnPropertyClick
import com.sbizzera.real_estate_manager.events.PropertyClickedListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment.ListPropertyFragmentViewModel.ListPropertyViewAction.AddPropertyClicked
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_list_property.*


class ListPropertyFragment : Fragment(), PropertyClickedListenable ,OnPropertyChangeListener{

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
        return inflater.inflate(R.layout.fragment_list_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerViewAdapter = ListPropertyFragmentAdapter()

        list_property_recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
            var itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
            getDrawable(this.context, R.drawable.divider)?.let { itemDecoration.setDrawable(it) }
            addItemDecoration(itemDecoration)
        }

        recyclerViewAdapter.onPropertyClickListener = onPropertyClickListener

        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory)
            .get(ListPropertyFragmentViewModel::class.java)

        viewModel.uiModel.observe(viewLifecycleOwner) { model ->
            with(recyclerViewAdapter) {
                list = model.properties
                notifyDataSetChanged()
            }
        }
        viewModel.listPropertyViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                AddPropertyClicked -> onPropertyClickListener.addPropertyClick(this)
            }
        }

        add_property_fab.setOnClickListener {
            viewModel.addPropertyClicked()
        }
        parentFragmentManager.setFragmentResultListener("REFRESH_PROPERTIES", viewLifecycleOwner) {_,_->
            println("debug : in here")
            viewModel.refreshProperties()
        }
    }

    override fun setListener(listener: OnPropertyClick) {
        onPropertyClickListener = listener
    }

    override fun onPropertyChange() {

    }


}