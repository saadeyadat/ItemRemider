package com.example.itemreminder.other.register

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository

class AppSignin(val context: Context) {

    fun checkUser(sharedPreferences: SharedPreferences, username: String, password: String): Boolean {
        var result = false
        var usersNumber = sharedPreferences.getInt("usersNumber", -1)
        while (usersNumber >= 0) {
            var savedUsername = sharedPreferences.getString("username+${usersNumber}", "")
            var savedPassword = sharedPreferences.getString("password+${usersNumber}", "")
            usersNumber--
            if (username == savedUsername)
                if (password == savedPassword)
                    result = true
                else
                    Toast.makeText(context, "Invalid Password.", Toast.LENGTH_SHORT).show()
        }
        if (!result)
            Toast.makeText(context, "User Does Not Exist.", Toast.LENGTH_SHORT).show()
        return result
    }
}