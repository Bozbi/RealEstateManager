package com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEvent
import com.sbizzera.real_estate_manager.events.OnUserAskTransactionEventListenable
import com.sbizzera.real_estate_manager.utils.ViewModelFactory
import com.sbizzera.real_estate_manager.utils.intent_contracts.GoToSettingContract

class MapFragment : SupportMapFragment(), OnMapReadyCallback, OnUserAskTransactionEventListenable {

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
                    AlertDialog.Builder(requireContext()).apply {
                        setPositiveButton("SETTINGS") { _, _ ->
                            registerForActivityResult(GoToSettingContract()) {}.launch(null)
                        }
                        setNegativeButton("BACK") { _, _ ->
                            activity?.supportFragmentManager?.popBackStack()
                        }
                        setMessage("Location permission is needed to access this feature")
                        setTitle("LOCATION PERMISSION")
                        setCancelable(false)
                    }.show()
                }
                MapViewModel.MapViewAction.GetMap -> getMapAsync(this)
                MapViewModel.MapViewAction.MapIsReadyWithoutLocation -> {
                    googleMap.isMyLocationEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true
                }
                is MapViewModel.MapViewAction.MapIsReady -> {
                    googleMap.isMyLocationEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                action.locationToFocus.latitude,
                                action.locationToFocus.longitude
                            ),
                            17f
                        )
                    )
                    action.markers.forEach {
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(it.latitude, it.longitude))
                        )
                        marker.tag = it.propertyId
                    }
                }
            }
        }
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapViewModel.mapIsReady()
        googleMap.setOnMarkerClickListener { marker ->
            if (marker.tag != null) {
                mapViewModel.markerIsClicked(marker.tag.toString())
            }
            onUserAskTransactionEvent.onPropertyDetailsAsked()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        mapViewModel.checkLocationPermission()
    }

    override fun onPause() {
        super.onPause()
        mapViewModel.mapIsNotReady()
    }

    override fun setListener(listener: OnUserAskTransactionEvent) {
        onUserAskTransactionEvent = listener
    }

}