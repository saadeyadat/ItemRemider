package com.example.itemreminder.other.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.itemreminder.R
import com.example.itemreminder.model.User

class SharedPrefManager constructor(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(R.string.app_name.toString(), AppCompatActivity.MODE_PRIVATE)

    fun setUser(user: User) {
        sharedPref.edit().putString(user.email, user.email).apply()
    }

    fun getUserEmail(): String {
        return sharedPref.getString("USER_EMAIL", "")!!
    }

    companion object {
        //lateinit var user: User
        private lateinit var instance: SharedPrefManager
        fun getInstance(context: Context): SharedPrefManager {
            if (!::instance.isInitialized)
                instance = SharedPrefManager(context)
            return instance
        }
    }
}