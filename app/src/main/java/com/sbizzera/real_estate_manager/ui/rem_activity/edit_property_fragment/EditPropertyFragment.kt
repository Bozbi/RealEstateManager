package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.utils.getTypeNameList
import com.sbizzera.real_estate_manager.events.*
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_edit_property.*

class EditPropertyFragment : Fragment(), OnPhotoSelectedListener, OnPhotoActionListener, OnUserAskTransactionEventListenable,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: EditPropertyViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: EditPropertyPhotoRecyclerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    lateinit var onUserAskTransactionEventListener: OnUserAskTransactionEvent

    companion object {
        fun newInstance() = EditPropertyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, ViewModelFactory).get(EditPropertyViewModel::class.java)
        viewModel.editUiStateLD.observe(viewLifecycleOwner) { model ->
            updateUi(model)
        }
        viewModel.editViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                EditPropertyViewModel.EditPropertyViewAction.TakePhotoFromCamera -> onUserAskTransactionEventListener.onLaunchCameraAsked()
                EditPropertyViewModel.EditPropertyViewAction.TakePhotoFromGallery -> onUserAskTransactionEventListener.onLaunchGalleryAsked()
                EditPropertyViewModel.EditPropertyViewAction.LaunchEditor -> onUserAskTransactionEventListener.onPhotoEditorAsked()
                is EditPropertyViewModel.EditPropertyViewAction.MoveRecyclerToPosition -> mLayoutManager.scrollToPosition(viewAction.position)
                is EditPropertyViewModel.EditPropertyViewAction.DisplayDatePicker -> {
                    val datePicker =
                        DatePickerDialog(requireContext(), this, viewAction.year, viewAction.month, viewAction.day)
                    datePicker.show()
                }
                EditPropertyViewModel.EditPropertyViewAction.FillInError -> Snackbar.make(
                    property_title_edt,
                    "Insert at least one photo and fill correctly the form",
                    Snackbar.LENGTH_LONG
                ).show()
                EditPropertyViewModel.EditPropertyViewAction.CloseFragment -> {
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

        recyclerViewAdapter.setListener(this)

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


    private fun updateUi(modelEdit: EditUiState) {
        recyclerViewAdapter.listPhotos = modelEdit.photoList
        recyclerViewAdapter.notifyDataSetChanged()
        property_title_edt.setText(modelEdit.propertyTitle)
        property_title_edt.error = modelEdit.propertyTitleError
        //TODO error for all textlayout not edt
        property_title_layout.error = modelEdit.propertyTypeError
        property_description_edt.setText(modelEdit.propertyDescription)
        property_description_edt.error = modelEdit.propertyDescriptionError
        property_address_edt.setText(modelEdit.propertyAddress)
        property_address_edt.error = modelEdit.propertyAddressError
        property_city_code_edt.setText(modelEdit.propertyCityCode)
        property_city_code_edt.error = modelEdit.propertyCityCodeError
        property_city_name_edt.setText(modelEdit.propertyCityName)
        property_city_name_edt.error = modelEdit.propertyCityNameError
        property_price_edt.setText(modelEdit.propertyPrice)
        property_price_edt.error = modelEdit.propertyPriceError
        property_type_autocomplete.setText(modelEdit.propertyType)
        property_type_autocomplete.error = modelEdit.propertyTypeError
        property_surface_edt.setText(modelEdit.propertySurface)
        property_surface_edt.error = modelEdit.propertySurfaceError
        property_room_count_edt.setText(modelEdit.propertyRoomCount)
        property_bedroom_count_edt.setText(modelEdit.propertyBedroomCount)
        property_bathroom_count_edt.setText(modelEdit.propertyBathroomCount)
        chip_school.isChecked = modelEdit.propertyPoiSchoolIsChecked
        chip_transport.isChecked = modelEdit.propertyPoiTransportIsChecked
        chip_shops.isChecked = modelEdit.propertyPoiShopIsChecked
        chip_parcs.isChecked = modelEdit.propertyPoiParcIsChecked
        chip_airport.isChecked = modelEdit.propertyPoiAirportIsChecked
        chip_down_town.isChecked = modelEdit.propertyPoiDownTownIsChecked
        chip_country_side.isChecked = modelEdit.propertyPoiCountrySideIsChecked
        property_sold_date_edt.setText(modelEdit.soldDate)
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

    override fun setListener(listener: OnUserAskTransactionEvent) {
        onUserAskTransactionEventListener = listener
    }

}
