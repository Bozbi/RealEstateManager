package com.openclassrooms.realestatemanager.ui.rem_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.Property
import com.openclassrooms.realestatemanager.data.PropertyRepository
import kotlinx.coroutines.launch
import java.util.*

class REMActivityViewModel(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    init {
//        populateDBforTesting()
    }

    private val _propertiesLiveData = MutableLiveData<List<Property>>()
    val propertiesLiveData = _propertiesLiveData


    fun insertProperty(propertyToInsert: Property) {
        viewModelScope.launch {
            propertyRepository.insertProperty(propertyToInsert)
        }
    }


    fun populateDBforTesting() {
        val propertyList = fakeProperties()
        propertyList.forEach {
            insertProperty(it)
        }
    }

    private fun fakeProperties(): List<Property> =
        listOf(
            Property(
                UUID.randomUUID().toString(),
                "Belle maison à BelAir",
                "Maison",
                350000
            ),
            Property(
                UUID.randomUUID().toString(),
                "Appartement avec cachet",
                "Appartement",
                250000
            ),
            Property(
                UUID.randomUUID().toString(),
                "Terrain à batir vue imprenable",
                "Terrain",
                100000
            ),
            Property(
                UUID.randomUUID().toString(),
                "Loft à rénover",
                "Appartement",
                450000
            ),
            Property(
                UUID.randomUUID().toString(),
                "Maison de ville à Fétilly",
                "Maison",
                550000
            ),
            Property(
                UUID.randomUUID().toString(),
                "Appartement à saisir urgemment",
                "Appartement",
                225000
            )
        )


}

