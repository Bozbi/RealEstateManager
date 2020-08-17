package com.sbizzera.real_estate_manager.ui.rem_activity.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.RangeSlider
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.model.PointOfInterest
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel.FilterDialogViewAction.CreationDateRangeClicked
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel.FilterDialogViewAction.SoldDateRangeClicked
import com.sbizzera.real_estate_manager.utils.architecture_components.ViewModelFactory
import com.sbizzera.real_estate_manager.custom_views.MyCustomChip
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.FilterUiState
import com.sbizzera.real_estate_manager.ui.rem_activity.list_property.ListPropertyViewModel
import kotlinx.android.synthetic.main.dialog_filter.*


class FilterDialog : DialogFragment() {
    companion object {
        fun newInstance() = FilterDialog()
    }

    private lateinit var dialogViewModel: ListPropertyViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setCanceledOnTouchOutside(true)
        return inflater.inflate(R.layout.dialog_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogViewModel = ViewModelProvider(requireActivity(),
            ViewModelFactory
        ).get(ListPropertyViewModel::class.java)

        price_range_slider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {}
            override fun onStopTrackingTouch(slider: RangeSlider) {
                dialogViewModel.onPriceRangeFilterChange(slider.values[0], slider.values[1])
            }
        })

        surface_range_slider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {}
            override fun onStopTrackingTouch(slider: RangeSlider) {
                dialogViewModel.onSurfaceRangeFilterChange(slider.values[0], slider.values[1])
            }
        })

        room_range_slider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {}
            override fun onStopTrackingTouch(slider: RangeSlider) {
                dialogViewModel.onRoomRangeFilterChange(slider.values[0], slider.values[1])
            }
        })

        addChips()

        dialogViewModel.filterUiState.observe(viewLifecycleOwner) {
            updateUiState(it)
        }

        dialogViewModel.filterViewAction.observe(viewLifecycleOwner) { action ->
            when (action) {
                is SoldDateRangeClicked -> {
                    val builder = MaterialDatePicker.Builder.dateRangePicker()
                    val datePicker = builder.build()
                    datePicker.addOnPositiveButtonClickListener { range ->
                        dialogViewModel.onSoldDateChange(range.first!!, range.second!!)
                    }
                    datePicker.show(activity?.supportFragmentManager!!, null)
                }
                is CreationDateRangeClicked -> {
                    val builder = MaterialDatePicker.Builder.dateRangePicker()
                    val datePicker = builder.build()
                    datePicker.addOnPositiveButtonClickListener { range ->
                        dialogViewModel.onCreationDateRangeChange(range.first!!, range.second!!)
                    }
                    datePicker.show(activity?.supportFragmentManager!!, null)
                }
            }
        }
        available_since_txt.setOnClickListener {
            dialogViewModel.onAvailableSinceDateClick()
        }
        available_since_layout.setEndIconOnClickListener {
            dialogViewModel.onAvailableDateCleared()
        }
        sold_since_txt.setOnClickListener {
            dialogViewModel.onSoldSinceDateClick()
        }
        sold_since_layout.setEndIconOnClickListener {
            dialogViewModel.onSoldSinceDateCleared()
        }
        reset_btn.setOnClickListener {
            dialogViewModel.resetFilters()
        }

    }

    private fun updateUiState(filterUiState: FilterUiState) {
        price_range_slider.setValues(
            filterUiState.priceMin,
            filterUiState.priceMax
        )
        surface_range_slider.setValues(
            filterUiState.surfaceMin,
            filterUiState.surfaceMax
        )
        room_range_slider.setValues(
            filterUiState.roomMin,
            filterUiState.roomMax
        )
        filterUiState.poiMap.forEach {
            chip_group.findViewWithTag<MyCustomChip>(it.key.name).updateIfDifferent(it.value)
        }
        available_since_txt.setText(filterUiState.availableAfter)
        sold_since_txt.setText(filterUiState.soldAfter)

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
                dialogViewModel.onChipCheckedChange(it.name, chip.isChecked)
            }
            chip_group.addView(chipToAdd)
        }
    }
}

