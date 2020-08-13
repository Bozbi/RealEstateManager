package com.sbizzera.real_estate_manager.utils

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.threeten.bp.LocalDate

@RunWith(PowerMockRunner::class)
@PrepareForTest(Utils::class, LocalDate::class)
class UtilsTest {

    private lateinit var utils: Utils

    @Before
    fun setUp() {
        val mockedDate = LocalDate.of(1983, 11, 29)
        PowerMockito.stub<LocalDate>(PowerMockito.method(LocalDate::class.java, "now")).toReturn(mockedDate)
        utils = Utils()
    }

    @Test
    fun convertDollarToEuro() {
        val dollars = 100
        assertEquals(utils.convertDollarToEuro(dollars), 81)
    }

    @Test
    fun getTodayDate() {
        assertEquals("29/11/1983", utils.getTodayDate())
    }
}