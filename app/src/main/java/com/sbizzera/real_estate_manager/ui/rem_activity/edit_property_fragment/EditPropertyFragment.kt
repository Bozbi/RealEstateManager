package com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.data.utils.getTypeNameList
import com.sbizzera.real_estate_manager.events.OnPhotoActionListener
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyViewModel.EditPropertyViewAction.*
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import com.sbizzera.real_estate_manager.utils.custom_views.MyCustomChip
import com.sbizzera.real_estate_manager.utils.intent_contracts.TakePictureContract
import com.sbizzera.real_estate_manager.utils.intent_contracts.TakePictureFromGalleryContract
import kotlinx.android.synthetic.main.fragment_edit_property.*

class EditPropertyFragment : Fragment(), OnPhotoActionListener, OnUserAskTransactionEventListenable,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: EditPropertyViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: EditPropertyPhotoRecyclerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var onUserAskTransactionEventListener: OnUserAskTransactionEvent
    private lateinit var onPropertySavedListener: OnPropertySavedListener


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
        viewModel.editEvent.observe(viewLifecycleOwner) {
            when (it) {
                EditPropertyViewModel.EditPropertyEvent.PropertySaved -> {
                    onPropertySavedListener.onPropertySaved()
                }
            }
        }
        viewModel.editViewAction.observe(viewLifecycleOwner) { viewAction ->
            when (viewAction) {
                is TakePhotoFromCamera -> {
                    registerForActivityResult(
                        TakePictureContract(fileHelper = viewAction.fileHelper)
                    ) {
                        it?.let { launchEditorWithUriReceived(it) }
                    }.launch(null)
                }

                is TakePhotoFromGallery -> {
                    registerForActivityResult(
                        TakePictureFromGalleryContract(fileHelper = viewAction.fileHelper)
                    ) {
                        it?.let { launchEditorWithUriReceived(it) }
                    }.launch(null)
                }
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
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }

        addChips()

        val propertyTypeAdapter = ArrayAdapter(
            requireContext(), R.layout.list_item,
            getTypeNameList()
        )
        val autoCompleteTextView = property_type_layout.editText as AutoCompleteTextView
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


        property_address_edt.afterTextChangedDelayed { address->
            viewModel.onAddressChange(address)
        }

        property_city_code_edt.afterTextChangedDelayed {cityCode->
            viewModel.onCityCodeChange(cityCode)

        }

        property_city_name_edt.afterTextChangedDelayed {cityName->
            viewModel.onCityNameChange(cityName)
        }

        property_price_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onPriceChange(text.toString())
        }

        property_type_autocomplete.doOnTextChanged { text, _, _, _ ->
            viewModel.onTypeChange(text.toString())
        }

        property_surface_edt.doOnTextChanged { text, _, _, _ ->
            viewModel.onSurfaceChange(text)
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

        scroll_view.setOnTouchListener { v, _ ->
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                scroll_view.windowToken,
                0
            )
            (v as ScrollView).children.forEach {
                it.clearFocus()
            }
            false
        }

        save_property_btn.setOnClickListener {
            viewModel.savePropertyClicked()
        }
    }

    private fun launchEditorWithUriReceived(it: String) {
        viewModel.onPhotoSelected(it)
    }

    private fun addChips() {
        PointOfInterest.values().forEach {
            val chipToAdd =
                MyCustomChip(chip_group.context)
            chipToAdd.text = it.label
            chipToAdd.isCheckable = true
            chipToAdd.isClickable = true
            chipToAdd.isFocusable = true
            chipToAdd.tag = it.name
            chipToAdd.setOnCheckedChangeListener { chip, _ ->
                viewModel.onChipChange(it.name, chip.isChecked)
            }
            chip_group.addView(chipToAdd)
        }
    }


    private fun updateUi(uiState: EditUiState) {
        recyclerViewAdapter.listPhotos = uiState.photoList
        recyclerViewAdapter.notifyDataSetChanged()
        add_photo_img.visibility = uiState.addPhotoVisibility
        property_title_edt.updateIfDifferent(uiState.propertyTitle)
        property_title_layout.error = uiState.propertyTitleError
        property_description_edt.updateIfDifferent(uiState.propertyDescription)
        property_description_layout.error = uiState.propertyDescriptionError
        property_address_edt.updateIfDifferent(uiState.propertyAddress)
        property_address_layout.error = uiState.propertyAddressError
        property_city_code_edt.updateIfDifferent(uiState.propertyCityCode)
        property_city_code_layout.error = uiState.propertyCityCodeError
        property_city_name_edt.updateIfDifferent(uiState.propertyCityName)
        property_city_name_layout.error = uiState.propertyCityNameError
        property_price_edt.updateIfDifferent(uiState.propertyPrice)
        property_price_layout.error = uiState.propertyPriceError
        property_type_autocomplete.updateIfDifferent(uiState.propertyType)
        property_type_layout.error = uiState.propertyTypeError
        property_surface_edt.updateIfDifferent(uiState.propertySurface)
        property_surface_layout.error = uiState.propertySurfaceError
        property_room_count_edt.updateIfDifferent(uiState.propertyRoomCount)
        property_bedroom_count_edt.updateIfDifferent(uiState.propertyBedroomCount)
        property_bathroom_count_edt.updateIfDifferent(uiState.propertyBathroomCount)
        property_sold_date_edt.updateIfDifferent(uiState.propertySoldDate)
        uiState.propertyPoiMap.forEach {
            chip_group.findViewWithTag<MyCustomChip>(it.key.name).updateIfDifferent(it.value)
        }
    }

    override fun onPhotoClick(position: Int) {
        viewModel.editPhotoClicked(position)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.onSoldDateChange(year, month, dayOfMonth)
    }

    override fun setListener(listener: OnUserAskTransactionEvent) {
        onUserAskTransactionEventListener = listener
    }

    fun setOnPropertySavedListener(onPropertySavedListener: OnPropertySavedListener) {
        this.onPropertySavedListener = onPropertySavedListener
    }

}
