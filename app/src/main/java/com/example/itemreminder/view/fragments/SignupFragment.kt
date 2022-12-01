package com.example.itemreminder.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.itemreminder.R
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.managers.FirebaseManager
import kotlinx.android.synthetic.main.signup_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupFragment(private val sharedPreferences: SharedPreferences, context: Context) : Fragment() {

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
        val username = signup_username?.text.toString()
        val password1 = signup_password1?.text.toString()
        val password2 = signup_password2?.text.toString()
        if (username.length>3 && username!="" && password1==password2 && password1!="" && password1.length>3) {
            regToShared()
            regToDatabase()
            regToFirebase()
            error_text?.text = ""
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        else
            error_text.text = "enter valid username or password"
    }

    private fun regToShared() {
        var usersNumber = sharedPreferences.getInt("usersNumber", -1)
        val PrefEdit = sharedPreferences.edit()
        usersNumber++
        PrefEdit.putString("username+${usersNumber}", signup_username?.text.toString()).apply()
        PrefEdit.putString("password+${usersNumber}", signup_password1?.text.toString()).apply()
        PrefEdit.putInt("usersNumber", usersNumber).apply()
    }

    private fun regToDatabase() {
        val email = signup_username?.text.toString()+"@gmail.com"
        val name = signup_username?.text.toString()
        GlobalScope.launch { Repository.getInstance(context).addUser(User(email, name)) }
    }

    private fun regToFirebase() {
        val email = signup_username?.text.toString()+"@gmail.com"
        val name = signup_username?.text.toString()
        FirebaseManager.getInstance(requireContext()).addUser(User(email, name))
    }
}