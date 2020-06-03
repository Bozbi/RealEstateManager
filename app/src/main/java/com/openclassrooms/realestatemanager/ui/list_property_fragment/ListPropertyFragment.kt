package com.openclassrooms.realestatemanager.ui.list_property_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utilities.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_list_property.view.*

class ListPropertyFragment : Fragment() {

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

        val recyclerViewAdapter = ListPropertyFragmentAdapter()

        view.listPropertyRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }

        val viewModel =
            ViewModelProvider(this, ViewModelFactory)
                .get(ListPropertyFragmentViewModel::class.java)

        viewModel.uiModel.observe(this) { model ->
            with(recyclerViewAdapter){
                notifyDataSetChanged()
                list = model.properties
            }
        }
        return view
    }
}