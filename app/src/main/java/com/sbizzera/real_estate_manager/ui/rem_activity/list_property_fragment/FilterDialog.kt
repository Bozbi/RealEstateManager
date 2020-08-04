package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.slider.RangeSlider
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.property.PointOfInterest
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import com.sbizzera.real_estate_manager.utils.custom_views.MyCustomChip
import kotlinx.android.synthetic.main.dialog_filter.*

class FilterDialog : DialogFragment(),DatePickerDialog.OnDateSetListener {
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
        dialogViewModel = ViewModelProvider(this, ViewModelFactory).get(ListPropertyViewModel::class.java)

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

        dialogViewModel.filterViewAction.observe(viewLifecycleOwner){action->
            when(action){
                is ListPropertyViewModel.FilterDialogViewAction.SoldDateClicked->{
                    val datePicker =
                        DatePickerDialog(requireContext(), this, action.year, action.month, action.day).
                    datePicker.show()
                }
                is ListPropertyViewModel.FilterDialogViewAction.AvailableDateClicked ->{
                    //TODO later
                }
            }
        }
        available_since_txt.setOnClickListener {
            dialogViewModel.onAvailableSinceDateClick()
        }
        sold_since_txt.setOnClickListener {
            dialogViewModel.onSoldSinceDateClick()
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
        available_since_txt.text = filterUiState.availableAfter
        availability_since_clear_img.visibility = filterUiState.clearAvailableSinceImageVisibility
        sold_since_txt.text = filterUiState.soldAfter
        sold_since_clear_img.visibility = filterUiState.clearSoldSinceImageVisibility

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
                dialogViewModel.onChipCheckedChange(it.name,chip.isChecked)
            }
            chip_group.addView(chipToAdd)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        //TODO This has to be down
    }
}

