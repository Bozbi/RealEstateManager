package com.sbizzera.real_estate_manager.data.utils

import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property_register.PropertyRegisterRow


fun Property.toPropertyRegisterRow() = PropertyRegisterRow(propertyId,modificationDate)
