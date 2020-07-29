package com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.ui.rem_activity.RationalePermissionDialog
import com.sbizzera.real_estate_manager.utils.ViewModelFactory

class MapFragment : SupportMapFragment(), OnMapReadyCallback ,OnUserAskTransactionEventListenable{

    private lateinit var googleMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var onUserAskTransactionEvent: OnUserAskTransactionEvent

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewModel = ViewModelProvider(this, ViewModelFactory).get(MapViewModel::class.java)

        mapViewModel.mapViewAction.observe(viewLifecycleOwner) { action ->
            when (action) {
                MapViewModel.MapViewAction.LocationPermissionToBeAsked -> {
                    mapViewModel.chooseAppropriateRequestPermission(
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
                MapViewModel.MapViewAction.RequestNormalPermission -> {
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                        mapViewModel.checkLocationPermission()
                    }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                MapViewModel.MapViewAction.RequestRationalePermission -> {
                    val dialog = RationalePermissionDialog.newInstance()
                    activity?.let {
                        dialog.show(it.supportFragmentManager,null)
                    }
//                    MaterialAlertDialogBuilder(context,R.style.ThemeOverlay_AppCompat_Dialog_Alert).apply {
//                        setTitle("Map Authorisation")
//                        setMessage("LocationPermission is Mandatory to access Map")
//                        setNegativeButton("BACK" ){_,_->
//
//                        }
//                        setPositiveButton("SETTINGS"){_,_->
//
//                        }
//                    }.show()
//                    AlertDialog.Builder(object :
//                        ContextThemeWrapper(requireContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert) {}).apply {
//                        setTitle("Map Authorisation")
//                        setMessage("LocationPermission is Mandatory to access Map")
//                        setNegativeButton("BACK", null)
//                        setPositiveButton("SETTINGS", null)
//                    }.show()
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
//                        registerForActivityResult(GoToSettingContract()) {
//                            mapViewModel.checkLocationPermission()
//                        }.launch()
//                    }
//
//                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
//                        alertDialog.dismiss()
//                    }

                }
                MapViewModel.MapViewAction.GetMap -> getMapAsync(this)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

    }

    override fun onResume() {
        super.onResume()
        mapViewModel.checkLocationPermission()
    }

    override fun setListener(listener: OnUserAskTransactionEvent) {
        onUserAskTransactionEvent = listener
    }


}