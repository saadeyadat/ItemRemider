package com.example.itemreminder.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.itemreminder.R
import com.example.itemreminder.viewModel.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_signup.*

class SignupFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onStart() {
        super.onStart()
        requireActivity().signup_username.setText(registrationViewModel.currentEmail)
        requireActivity().signup_username.addTextChangedListener { registrationViewModel.currentEmail = it.toString() }
    }

    fun createUser(sharedPreferences: SharedPreferences) {
        var usersNumber = sharedPreferences.getInt("usersNumber", -1)
        val save = sharedPreferences.edit()
        if (signup_username?.text.toString().length>3 && signup_username?.text.toString()!="" && signup_password1?.text.toString()==signup_password2?.text.toString() && signup_password1?.text.toString()!="" && signup_password1?.text.toString().length>3) {
            usersNumber++
            save.putString("username+${usersNumber}", signup_username?.text.toString()).apply()
            save.putString("password+${usersNumber}", signup_password1?.text.toString()).apply()
            save.putInt("usersNumber", usersNumber).apply()
            error_text?.text = ""
            signup_username?.setText("")
            signup_password1?.setText("")
            signup_password2?.setText("")
        }
        else
            error_text.text = "enter valid username or password"
    }
}