package com.example.metronom2

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("creatorSettings", Context.MODE_PRIVATE)

    //zapis
    fun saveSetting(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    //odczyt
    fun getSetting(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}