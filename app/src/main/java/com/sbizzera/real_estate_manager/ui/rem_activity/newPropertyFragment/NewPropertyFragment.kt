package com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.property.getTypeNameList
import com.sbizzera.real_estate_manager.events.OnPhotoEditClickListener
import com.sbizzera.real_estate_manager.events.OnPhotoSelectedListener
import com.sbizzera.real_estate_manager.events.SelectPhotoSourceListener
import com.sbizzera.real_estate_manager.ui.rem_activity.newPropertyFragment.NewPropertyFragmentViewModel.NewPropertyViewAction.*
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_new_property.*
import kotlinx.android.synthetic.main.fragment_new_property.view.*

class NewPropertyFragment : Fragment(), OnPhotoSelectedListener, OnPhotoEditClickListener,
    DatePickerDialog.OnDateSetListener {


    private lateinit var viewModel: NewPropertyFragmentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: NewPropertyPhotoRecyclerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    lateinit var listener: SelectPhotoSourceListener

    companion object {
        fun newInstance() = NewPropertyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_property, container, false)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory).get(NewPropertyFragmentViewModel::class.java)
        viewModel.uiModel.observe(this) { model: UiModel ->
            updateUi(model)
        }
        viewModel.viewAction.observe(this) { viewAction ->
            when (viewAction) {
                TakePhotoFromCamera -> listener.onLaunchCameraClick()
                TakePhotoFromGallery -> listener.onLaunchGalleryClick()
                LaunchEditor -> listener.onPhotoEditorLaunch()
                is MoveRecyclerToPosition -> mLayoutManager.scrollToPosition(viewAction.position)
                is DisplayDatePicker -> {
                    val datePicker = DatePickerDialog(requireContext(), this, viewAction.year, viewAction.month, viewAction.day)
                    datePicker.show()
                }
                PhotoListError -> Snackbar.make(property_title_edt,"You should had at least one photo",Snackbar.LENGTH_LONG).show()
                TitleError -> property_title_edt.error = "Title must be filled in"
                DescriptionError -> property_description_edt.error = "Description must be filled in"
                AddressError -> property_address_edt.error = "Address must be filled in"
                CityCodeError -> property_city_code_edt.error = "City code must be filled in"
                CityNameError -> property_city_name_edt.error = "City name must be filled in"
                PriceError -> property_price_edt.error = "Price must be filled in"
                TypeError -> property_type_autocomplete.error = "Type must be filled in"
                SurfaceError -> property_surface_edt.error = "Surface must be filled in"
                FillInError -> Snackbar.make(property_title_edt,"All necessary fields need to be fill in",Snackbar.LENGTH_LONG).show()
            }
        }


        val propertyTypeAdapter = ArrayAdapter(requireContext(),R.layout.list_item, getTypeNameList())
        val autoCompleteTextView =  view.property_type_edt.editText as AutoCompleteTextView
        autoCompleteTextView.setAdapter(propertyTypeAdapter)

        recyclerViewAdapter = NewPropertyPhotoRecyclerAdapter()
        mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()

        recyclerView = view.property_photos_recycler_view.apply {
            layoutManager = mLayoutManager
            adapter = recyclerViewAdapter
        }
        recyclerViewAdapter.listener = this
        snapHelper.attachToRecyclerView(view.property_photos_recycler_view)

        view.add_photo_from_camera_btn.setOnClickListener {
            viewModel.takePhotoFromCameraClicked()
        }

        view.add_photo_from_gallery_btn.setOnClickListener {
            viewModel.takePhotoFromGalleryClicked()
        }

        view.property_sold_date_edt.setOnClickListener {
            viewModel.soldDatePickerClicked()
        }

        view.save_property_btn.setOnClickListener {
            viewModel.savePropertyClicked(
                property_title_edt.text.toString(),
                property_description_edt.text.toString(),
                property_address_edt.text.toString(),
                property_city_code_edt.text.toString(),
                property_city_name_edt.text.toString(),
                property_price_edt.text.toString(),
                property_type_autocomplete.text.toString(),
                property_surface_edt.text.toString(),
                property_room_count_edt.text.toString(),
                property_bedroom_count_edt.text.toString(),
                property_bathroom_count_edt.text.toString(),
                chip_school.isChecked,
                chip_transport.isChecked,
                chip_shops.isChecked,
                chip_parcs.isChecked,
                chip_airport.isChecked,
                chip_down_town.isChecked,
                chip_country_side.isChecked,
                property_sold_date_edt.text.toString()
            )
        }


        return view
    }


    private fun updateUi(model: UiModel) {
        recyclerViewAdapter.listPhotos = model.photoList
        recyclerViewAdapter.notifyDataSetChanged()
        property_sold_date_edt.setText(model.soldDate)
    }

    override fun onPhotoSelected(uri: String) {
        viewModel.onPhotoSelected(uri)
    }

    override fun onPhotoEditClick(position: Int) {
        viewModel.editPhotoClicked(position)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.setSoldDate(year,month,dayOfMonth)
    }
}
