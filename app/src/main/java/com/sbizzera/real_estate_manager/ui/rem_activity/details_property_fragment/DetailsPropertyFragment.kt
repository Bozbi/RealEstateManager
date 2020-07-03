package com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.details_property_fragment.*

class DetailsPropertyFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsPropertyFragment()
    }

    private lateinit var viewModel: DetailsPropertyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.details_property_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity(),ViewModelFactory).get(DetailsPropertyViewModel::class.java)
        recycler_view.adapter = DetailsPropertyPhotoAdapter()
        recycler_view.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }

}
