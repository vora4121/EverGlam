package com.android.everglam.utils

import android.content.Context
import android.content.SharedPreferences
import com.android.everglam.data.ScannedData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class AppPreferencesHelper(context: Context, prefFileName: String) {

    companion object{
        const val USER_TYPE = "user_type"
        const val USER_IS_LOGIN = "user_is_login"
        const val SAVE_ALL_DATA = "all_data"
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

    fun <T> setProductData(key: String?, list: ArrayList<T>?) {
        val gson = Gson()
        val json = gson.toJson(list)
        setString(key.toString(), json!!)
    }

    fun getList(): ArrayList<ScannedData> {
        val arrayItems: ArrayList<ScannedData>
        val serializedObject: String = getString(SAVE_ALL_DATA, "")!!
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<ScannedData>>() {}.type
        arrayItems = gson.fromJson<ArrayList<ScannedData>>(serializedObject, type)
        return arrayItems
    }


}