package com.sbizzera.real_estate_manager.utils

import android.content.Context
import android.net.ConnectivityManager
import kotlin.math.roundToInt

class Utils {

    fun convertDollarToEuro(dollars: Int) = (dollars * 0.812).roundToInt()

    fun getTodayDate(): String = org.threeten.bp.LocalDate.now().format(CUSTOM_DATE_FORMATTER)

    fun isNetworkAvailable(context: Context):Boolean{
        val activeNetworkInfo = (context.getSystemService((Context.CONNECTIVITY_SERVICE)) as ConnectivityManager).activeNetworkInfo
        return activeNetworkInfo!=null && activeNetworkInfo.isConnectedOrConnecting
    }

}


