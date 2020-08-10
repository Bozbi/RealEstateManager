package com.sbizzera.real_estate_manager.utils

import android.content.Context
import com.sbizzera.real_estate_manager.App

class SharedPreferencesRepo private constructor(private val appContext:App){
    companion object{
        val instance by lazy {
            SharedPreferencesRepo(App.instance)
        }
    }

    fun insertUserName(username:String?):Boolean{
        val sharedPref = appContext.getSharedPreferences("user_info_file",Context.MODE_PRIVATE)
        return sharedPref.edit().putString("USER_NAME",username).commit()
    }

    fun getUserName():String?{
        val sharedPref = appContext.getSharedPreferences("user_info_file",Context.MODE_PRIVATE)
        return sharedPref.getString("USER_NAME",null)
    }
}