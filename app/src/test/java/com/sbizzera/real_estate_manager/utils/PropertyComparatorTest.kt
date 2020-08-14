package com.sbizzera.real_estate_manager.utils

import com.sbizzera.real_estate_manager.data.model.Property
import com.sbizzera.real_estate_manager.utils.data_utils.PropertyComparator
import org.junit.Test

import org.junit.Assert.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class PropertyComparatorTest {

    @Test
    fun compare() {
        val date1 = LocalDateTime.of(2000,1,1,1,1,1).format(DateTimeFormatter.ISO_DATE_TIME)
        val date2 = LocalDateTime.of(2000,1,1,1,1,2).format(DateTimeFormatter.ISO_DATE_TIME)
        val property1 = Property(creationDate = date1)
        val property2 = Property(creationDate = date2)
        val list = listOf(property1,property2)
        Collections.sort(list, PropertyComparator())
        assertEquals(list[0],property2)
    }
}