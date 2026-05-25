package com.mansi.aiinsight.ui.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("aiinsight_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun <T> saveObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        saveString(key, json)
    }

    private inline fun <reified T> getObject(key: String, defaultValue: T? = null): T? {
        val json = getString(key)
        return if (json.isEmpty()) defaultValue else gson.fromJson(json, T::class.java)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}