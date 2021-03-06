package com.sbizzera.real_estate_manager.utils.data_utils

import com.sbizzera.real_estate_manager.data.model.Property
import org.threeten.bp.format.DateTimeFormatter

class PropertyComparator : Comparator<Property> {
    override fun compare(property1: Property, property2: Property): Int {
        val creationDate1 = org.threeten.bp.LocalDateTime.parse(property1.creationDate, DateTimeFormatter.ISO_DATE_TIME)
        val creationDate2 = org.threeten.bp.LocalDateTime.parse(property2.creationDate, DateTimeFormatter.ISO_DATE_TIME)
        return creationDate2.compareTo(creationDate1)
    }
}