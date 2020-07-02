package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.utils.getTypeNameList
import com.sbizzera.real_estate_manager.events.OnPhotoEditClickListener
import com.sbizzera.real_estate_manager.events.OnPhotoSelectedListener
import com.sbizzera.real_estate_manager.events.OnPropertyChangeListener
import com.sbizzera.real_estate_manager.events.SelectPhotoSourceListener
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyFragmentViewModel.EditPropertyViewAction.*
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_edit_property.*

class EditPropertyFragment : Fragment(), OnPhotoSelectedListener, OnPhotoEditClickListener,
    DatePickerDialog.OnDateSetListener {

    lateinit var onPropertyChangeListener: OnPropertyChangeListener
    private lateinit var viewModel: EditPropertyFragmentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: EditPropertyPhotoRecyclerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    lateinit var listener: SelectPhotoSourceListener

    companion object {
        fun newInstance() = EditPropertyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory).get(EditPropertyFragmentViewModel::class.java)
        viewModel.editPropertyUiModel.observe(viewLifecycleOwner) { modelEditProperty: EditPropertyUiModel ->
            updateUi(modelEditProperty)
        }
        viewModel.viewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                TakePhotoFromCamera -> listener.onLaunchCameraClick()
                TakePhotoFromGallery -> listener.onLaunchGalleryClick()
                LaunchEditor -> listener.onPhotoEditorLaunch()
                is MoveRecyclerToPosition -> mLayoutManager.scrollToPosition(viewAction.position)
                is DisplayDatePicker -> {
                    val datePicker =
                        DatePickerDialog(requireContext(), this, viewAction.year, viewAction.month, viewAction.day)
                    datePicker.show()
                }
                FillInError -> Snackbar.make(
                    property_title_edt,
                    "Insert at least one photo and fill correctly the form",
                    Snackbar.LENGTH_LONG
                ).show()
                CloseFragment -> {
                    onPropertyChangeListener.onPropertyChange()
                    parentFragmentManager.setFragmentResult("REFRESH_PROPERTIES", bundleOf())
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }
            }
        }


        val propertyTypeAdapter = ArrayAdapter(
            requireContext(), R.layout.list_item,
            getTypeNameList()
        )
        val autoCompleteTextView = property_type_edt.editText as AutoCompleteTextView
        autoCompleteTextView.setAdapter(propertyTypeAdapter)

        recyclerViewAdapter = EditPropertyPhotoRecyclerAdapter()
        mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()

        recyclerView = property_photos_recycler_view.apply {
            layoutManager = mLayoutManager
            adapter = recyclerViewAdapter
        }
        recyclerViewAdapter.listener = this
        snapHelper.attachToRecyclerView(property_photos_recycler_view)

        add_photo_from_camera_btn.setOnClickListener {
            viewModel.takePhotoFromCameraClicked()
        }

        add_photo_from_gallery_btn.setOnClickListener {
            viewModel.takePhotoFromGalleryClicked()
        }

        property_sold_date_edt.setOnClickListener {
            viewModel.soldDatePickerClicked()
        }

        save_property_btn.setOnClickListener {
            viewModel.savePropertyClicked(
                recyclerViewAdapter.listPhotos,
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
    }


    private fun updateUi(modelEditProperty: EditPropertyUiModel) {
        recyclerViewAdapter.listPhotos = modelEditProperty.photoList
        recyclerViewAdapter.notifyDataSetChanged()
        property_title_edt.setText(modelEditProperty.propertyTitle)
        property_title_edt.error = modelEditProperty.propertyTitleError
        //TODO error for all textlayout not edt
        property_title_layout.error = modelEditProperty.propertyTypeError
        property_description_edt.setText(modelEditProperty.propertyDescription)
        property_description_edt.error = modelEditProperty.propertyDescriptionError
        property_address_edt.setText(modelEditProperty.propertyAddress)
        property_address_edt.error = modelEditProperty.propertyAddressError
        property_city_code_edt.setText(modelEditProperty.propertyCityCode)
        property_city_code_edt.error = modelEditProperty.propertyCityCodeError
        property_city_name_edt.setText(modelEditProperty.propertyCityName)
        property_city_name_edt.error = modelEditProperty.propertyCityNameError
        property_price_edt.setText(modelEditProperty.propertyPrice)
        property_price_edt.error = modelEditProperty.propertyPriceError
        property_type_autocomplete.setText(modelEditProperty.propertyType)
        property_type_autocomplete.error = modelEditProperty.propertyTypeError
        property_surface_edt.setText(modelEditProperty.propertySurface)
        property_surface_edt.error = modelEditProperty.propertySurfaceError
        property_room_count_edt.setText(modelEditProperty.propertyRoomCount)
        property_bedroom_count_edt.setText(modelEditProperty.propertyBedroomCount)
        property_bathroom_count_edt.setText(modelEditProperty.propertyBathroomCount)
        chip_school.isChecked = modelEditProperty.propertyPoiSchoolIsChecked
        chip_transport.isChecked = modelEditProperty.propertyPoiTransportIsChecked
        chip_shops.isChecked = modelEditProperty.propertyPoiShopIsChecked
        chip_parcs.isChecked = modelEditProperty.propertyPoiParcIsChecked
        chip_airport.isChecked = modelEditProperty.propertyPoiAirportIsChecked
        chip_down_town.isChecked = modelEditProperty.propertyPoiDownTownIsChecked
        chip_country_side.isChecked = modelEditProperty.propertyPoiCountrySideIsChecked
        property_sold_date_edt.setText(modelEditProperty.soldDate)
    }

    override fun onPhotoSelected(uri: String) {
        viewModel.onPhotoSelected(uri)
    }

    override fun onPhotoEditClick(position: Int) {
        viewModel.editPhotoClicked(position)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.setSoldDate(year, month, dayOfMonth)
    }

}
