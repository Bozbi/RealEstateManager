package com.sbizzera.real_estate_manager.ui.rem_activity.map_fragment

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.sbizzera.real_estate_manager.utils.ViewModelFactory

class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewModel = ViewModelProvider(requireActivity(), ViewModelFactory).get(MapViewModel::class.java)
        mapViewModel.mapViewAction.observe(viewLifecycleOwner) { action ->
            when (action) {
                MapViewModel.MapViewAction.LocationPermissionToBeAsked ->
                    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { mapOfPermissions ->
                        if (mapOfPermissions.all { entry ->
                                println("debug : permission ${entry.key} granted ${entry.value}")
                                entry.value == true
                            }) {
                            getMapAsync(this)
                        } else {

                        }
                    }
                        .launch(
                            arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
    }
}