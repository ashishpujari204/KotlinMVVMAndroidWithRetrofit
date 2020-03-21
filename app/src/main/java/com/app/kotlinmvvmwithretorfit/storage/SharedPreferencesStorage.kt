package com.app.smartprocessors.storage


import android.content.Context
import com.app.smartprocessors.util.Constants

class SharedPreferencesStorage(context: Context) : Storage {

    private val sharedPreferences = context.getSharedPreferences(Constants.PREFERANCE_NAME, Context.MODE_PRIVATE)

    override fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun setInt(key: String, value: Int) {
        with(sharedPreferences.edit()) {
            putInt(key, value)
            apply()
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }
    override fun getInt(key: String): Int {
        return sharedPreferences.getInt(key,0)!!
    }
}
