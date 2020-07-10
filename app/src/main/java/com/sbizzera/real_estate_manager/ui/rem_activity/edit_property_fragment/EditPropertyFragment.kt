package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.utils.getTypeNameList
import com.sbizzera.real_estate_manager.events.OnPhotoActionListener
import com.sbizzera.real_estate_manager.events.OnPhotoSelectedListener
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyViewModel.EditPropertyViewAction.*
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import com.sbizzera.real_estate_manager.utils.updateIsChecked
import com.sbizzera.real_estate_manager.utils.updateTextIfDifferent
import com.sbizzera.real_estate_manager.utils.updateTypeIfDifferent
import kotlinx.android.synthetic.main.fragment_edit_property.*

class EditPropertyFragment : Fragment(), OnPhotoSelectedListener, OnPhotoActionListener, OnUserAskTransactionEventListenable,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: EditPropertyViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: EditPropertyPhotoRecyclerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var onUserAskTransactionEventListener: OnUserAskTransactionEvent

    companion object {
        fun newInstance() = EditPropertyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_property, container, false)
    }

    private fun addChips() {
        //TODO How To insert This in VM is it mandatory
        PointOfInterest.values().forEach {
            val chipToAdd = Chip(chip_group.context)
            chipToAdd.text = it.value
            chipToAdd.isCheckable = true
            chipToAdd.isFocusable = true
            chipToAdd.isClickable = true
            chipToAdd.tag = it.value
            chipToAdd.setOnCheckedChangeListener { chip, _ ->
                viewModel.onChipChange(chip.tag.toString(), chip.isChecked)
            }
            chip_group.addView(chipToAdd)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, ViewModelFactory).get(EditPropertyViewModel::class.java)
        viewModel.editUiStateLD.observe(viewLifecycleOwner) { model ->
            updateUi(model)
        }
        viewModel.editViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                TakePhotoFromCamera -> onUserAskTransactionEventListener.onLaunchCameraAsked()
                TakePhotoFromGallery -> onUserAskTransactionEventListener.onLaunchGalleryAsked()
                LaunchEditor -> onUserAskTransactionEventListener.onPhotoEditorAsked()
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
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }
            }
        }

        addChips()

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

        property_title_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onTitleChange(text.toString())
        }

        property_description_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChange(text.toString())
        }

        property_address_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onAddressChange(text.toString())
        }

        property_city_code_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onCityCodeChange(text.toString())
        }

        property_city_name_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onCityNameChange(text.toString())
        }

        property_price_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onPriceChange(text.toString())
        }

        property_type_autocomplete.doOnTextChanged { text, _, _, _ ->
            viewModel.onTypeChange(text.toString())
        }

        property_surface_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onSurfaceChange(text.toString())
        }

        property_room_count_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onRoomCountChange(text.toString())
        }

        property_bedroom_count_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onBedroomCountChange(text.toString())
        }

        property_bathroom_count_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onBathroomCountChange(text.toString())
        }


//        save_property_btn.setOnClickListener {
//            viewModel.savePropertyClicked(
//                recyclerViewAdapter.listPhotos,
//                property_title_edt.text.toString(),
//                property_description_edt.text.toString(),
//                property_address_edt.text.toString(),
//                property_city_code_edt.text.toString(),
//                property_city_name_edt.text.toString(),
//                property_price_edt.text.toString(),
//                property_type_autocomplete.text.toString(),
//                property_surface_edt.text.toString(),
//                property_room_count_edt.text.toString(),
//                property_bedroom_count_edt.text.toString(),
//                property_bathroom_count_edt.text.toString(),
//                chip_school.isChecked,
//                chip_transport.isChecked,
//                chip_shops.isChecked,
//                chip_parcs.isChecked,
//                chip_airport.isChecked,
//                chip_down_town.isChecked,
//                chip_country_side.isChecked,
//                property_sold_date_edt.text.toString()
//            )
//        }
    }


    private fun updateUi(uiState: EditUiState) {
        recyclerViewAdapter.listPhotos = uiState.photoList
        recyclerViewAdapter.notifyDataSetChanged()
        property_title_edt.updateTextIfDifferent(uiState.propertyTitle)
        property_title_edt.error = uiState.propertyTitleError
        //TODO error for all textlayout not edt
        property_title_layout.error = uiState.propertyTypeError
        property_description_edt.updateTextIfDifferent(uiState.propertyDescription)
        property_description_edt.error = uiState.propertyDescriptionError
        property_address_edt.updateTextIfDifferent(uiState.propertyAddress)
        property_address_edt.error = uiState.propertyAddressError
        property_city_code_edt.updateTextIfDifferent(uiState.propertyCityCode)
        property_city_code_edt.error = uiState.propertyCityCodeError
        property_city_name_edt.updateTextIfDifferent(uiState.propertyCityName)
        property_city_name_edt.error = uiState.propertyCityNameError
        property_price_edt.updateTextIfDifferent(uiState.propertyPrice)
        property_price_edt.error = uiState.propertyPriceError
        property_type_autocomplete.updateTypeIfDifferent(uiState.propertyType)
        property_type_autocomplete.error = uiState.propertyTypeError
        property_surface_edt.updateTextIfDifferent(uiState.propertySurface)
        property_surface_edt.error = uiState.propertySurfaceError
        property_room_count_edt.updateTextIfDifferent(uiState.propertyRoomCount)
        property_bedroom_count_edt.updateTextIfDifferent(uiState.propertyBedroomCount)
        property_bathroom_count_edt.updateTextIfDifferent(uiState.propertyBathroomCount)
        property_sold_date_edt.updateTextIfDifferent(uiState.propertySoldDate)
        uiState.propertyPoiMap.forEach {
            chip_group.findViewWithTag<Chip>(it.key).updateIsChecked(it.value)
        }
    }

    override fun onPhotoSelected(uri: String) {
        viewModel.onPhotoSelected(uri)
    }

    override fun onPhotoEditClick(position: Int) {
        viewModel.editPhotoClicked(position)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.onSoldDateChange(year, month, dayOfMonth)
    }

    override fun setListener(listener: OnUserAskTransactionEvent) {
        onUserAskTransactionEventListener = listener
    }

}
