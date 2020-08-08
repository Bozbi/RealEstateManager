package com.sbizzera.real_estate_manager.utils

import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.JUnitCore

import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class UtilsAndroidTest{

    @Test
    fun isNetworkAvailable() {
        Assert.assertEquals(true,Utils().isNetworkAvailable(InstrumentationRegistry.getInstrumentation().context))
    }
}
