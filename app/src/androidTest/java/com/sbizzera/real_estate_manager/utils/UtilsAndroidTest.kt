package com.sbizzera.real_estate_manager.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test

import org.junit.runner.RunWith
import org.threeten.bp.LocalDate


@RunWith(AndroidJUnit4::class)
class UtilsAndroidTest{

    @Test
    fun isNetworkAvailable() {
        Assert.assertEquals(true,Utils().isNetworkAvailable(InstrumentationRegistry.getInstrumentation().context))
    }

    @Test
    fun getTodayDate() {
        val date = LocalDate.now().format(CUSTOM_DATE_FORMATTER)
        Assert.assertEquals(date, Utils().getTodayDate())
    }
}
