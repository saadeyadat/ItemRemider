package com.example.itemreminder.other.register

import android.content.SharedPreferences

class AppSignin {

    fun checkUser(sharedPreferences: SharedPreferences, username: String, password: String): Boolean {
        var result = false
        var usersNumber = sharedPreferences.getInt("usersNumber", -1)
        while (usersNumber >= 0) {
            var savedUsername = sharedPreferences.getString("username+${usersNumber}", "")
            var savedPassword = sharedPreferences.getString("password+${usersNumber}", "")
            usersNumber--
            if (username == savedUsername && password == savedPassword)
                result = true
        }
        return result
    }
}