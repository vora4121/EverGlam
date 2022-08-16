package com.android.everglam.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class AppPreferencesHelper(context: Context, prefFileName: String) {

    companion object{
        const val USER_TYPE = "user_type"
        const val USER_IS_LOGIN = "user_is_login"
    }

    private var prefs: SharedPreferences? = null
    private var gson: Gson = Gson()
    init {
        prefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    }

    fun setString(key: String, value: String) {
        prefs!!.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return prefs!!.getString(key, defaultValue)
    }

    fun setBoolean(key: String, value : Boolean){
        prefs!!.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean) : Boolean?{
        return prefs!!.getBoolean(key, defaultValue)
    }

}