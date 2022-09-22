package com.example.itemreminder.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.itemreminder.view.activities.ItemsActivity
import com.example.itemreminder.R
import com.example.itemreminder.viewModel.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        requireActivity().login_username.setText(registrationViewModel.currentEmail)
        requireActivity().login_username.addTextChangedListener { registrationViewModel.currentEmail = it.toString() }
    }

    fun startLogin(context: Context, sharedPreferences: SharedPreferences) {
        val intent = Intent(context, ItemsActivity::class.java)
        val timeEdit = sharedPreferences.edit()
        timeEdit.putLong("LAST_LOGIN", System.currentTimeMillis()).apply()
        if (checkUser(login_username?.text.toString(), login_password?.text.toString(), sharedPreferences))
            startActivity(intent)
    }

    private fun checkUser(username: String, password: String, sharedPreferences: SharedPreferences): Boolean {
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