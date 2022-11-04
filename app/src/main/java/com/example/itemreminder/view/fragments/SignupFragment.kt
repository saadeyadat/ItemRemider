package com.example.itemreminder.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.itemreminder.R
import kotlinx.android.synthetic.main.signup_fragment.*

class SignupFragment(private val sharedPreferences: SharedPreferences) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.signup_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        signup_button.setOnClickListener { createUser() }
        signin_text.setOnClickListener { parentFragmentManager.beginTransaction().remove(this).commit()}
    }

    private fun createUser() {
        var usersNumber = sharedPreferences.getInt("usersNumber", -1)
        val save = sharedPreferences.edit()
        if (signup_username?.text.toString().length>3 && signup_username?.text.toString()!="" && signup_password1?.text.toString()==signup_password2?.text.toString() && signup_password1?.text.toString()!="" && signup_password1?.text.toString().length>3) {
            usersNumber++
            save.putString("username+${usersNumber}", signup_username?.text.toString()).apply()
            save.putString("password+${usersNumber}", signup_password1?.text.toString()).apply()
            save.putInt("usersNumber", usersNumber).apply()
            error_text?.text = ""
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        else
            error_text.text = "enter valid username or password"
    }

    private fun addUserToDatabase() {
        if (signup_username?.text.toString().length>3 && signup_username?.text.toString()!="" && signup_password1?.text.toString()==signup_password2?.text.toString() && signup_password1?.text.toString()!="" && signup_password1?.text.toString().length>3) {
            error_text?.text = ""
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        else
            error_text.text = "enter valid username or password"
    }
}