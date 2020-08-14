package com.sbizzera.real_estate_manager.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test

import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UtilsAndroidTest{

    @Test
    fun isNetworkAvailable() {
        Assert.assertEquals(true,Utils().isNetworkAvailable(InstrumentationRegistry.getInstrumentation().context))
    }

    @Test
    fun getTodayDate() {
        Assert.assertEquals("29/11/1983", getTodayDate())
    }
}
