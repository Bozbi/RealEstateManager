package com.sbizzera.real_estate_manager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.ConnectivityManagerCompat
import com.sbizzera.real_estate_manager.App
import kotlin.math.roundToInt

class Utils {
    fun convertDollarToEuro(dollars: Int) = (dollars * 0.812).roundToInt()
    fun getTodayDate(): String = org.threeten.bp.LocalDate.now().format(CUSTOM_DATE_FORMATTER)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun isNetworkAvailable(context: Context):Boolean{
        val activeNetworkInfo = (context.getSystemService((Context.CONNECTIVITY_SERVICE)) as ConnectivityManager).activeNetworkInfo
        return activeNetworkInfo!=null && activeNetworkInfo.isConnectedOrConnecting
    }
}


