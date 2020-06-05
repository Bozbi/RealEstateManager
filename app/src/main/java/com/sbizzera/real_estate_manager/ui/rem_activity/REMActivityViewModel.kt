package com.sbizzera.real_estate_manager.ui.rem_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyRepository
import com.sbizzera.real_estate_manager.data.property_register.PropertyRegisterRepository
import com.sbizzera.real_estate_manager.data.utils.toPropertyRegisterRow
import com.sbizzera.real_estate_manager.utils.FAKE_PROPERTIES
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

class REMActivityViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyRegisterRepository: PropertyRegisterRepository
) : ViewModel() {

    init {
//        populateDbForTesting()
//        compareRegistersAndReact()
    }

    //TODO See this later
    private fun compareRegistersAndReact() {
        viewModelScope.launch {
            val localList = propertyRegisterRepository.getLocalPropertyRegister()
            val localRegister = localList.associateBy({ it.propertyId }, { it.lastModification })
            val remoteRegister = propertyRegisterRepository.getRemotePropertyRegister().register
            localRegister.forEach {
                if (remoteRegister.containsKey(it.key)) {
                    if (remoteRegister[it.key] != it.value) {
                        val localTimeOfModification = LocalDateTime.parse(it.value)
                        val remoteTimeOfLastModification =
                            LocalDateTime.parse(remoteRegister[it.key])
                        if (localTimeOfModification.isAfter(remoteTimeOfLastModification)) {
                            remoteRegister[it.key] = it.value
                        }
                    }
                }
                if (it !in remoteRegister.entries) {
                    remoteRegister[it.key] = it.value
                }
            }
            propertyRegisterRepository.updateRemoteRegister(remoteRegister)
        }
    }

    private fun insertProperty(propertyToInsert: Property) {
        viewModelScope.launch {
            propertyRepository.insertLocalProperty(propertyToInsert)
            propertyRegisterRepository.insertPropertyInLocalRegister(propertyToInsert.toPropertyRegisterRow())
        }
    }


    private fun populateDbForTesting() {
        val propertyList = FAKE_PROPERTIES
        propertyList.forEach {
            insertProperty(it)
        }
    }

}

