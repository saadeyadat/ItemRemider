package com.example.itemreminder.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        signin_text.setOnClickListener { parentFragmentManager.beginTransaction().remove(this).commit() }
    }

    private fun createUser() {
        val email = signup_email?.text.toString()
        val password1 = signup_password1?.text.toString()
        val password2 = signup_password2?.text.toString()
        if (checkUsername(email) && checkPassword(password1, password2)) {
            regToShared()
            regToDatabase()
            regToFirebase()
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun checkUsername(email: String): Boolean {
        if (email.contains('@') && email.length>7 && email!="")
            return true
        Toast.makeText(requireContext(), "Please Enter Valid Email.", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun checkPassword(password1: String, password2: String): Boolean {
        if (password1==password2 && password1!="")
            if ( password1.length>=8)
                return true
            else
                Toast.makeText(requireContext(), "Password must be at least 8 digits.", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(requireContext(), "Passwords must be the same.", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun regToShared() {
        var usersNumber = sharedPreferences.getInt("usersNumber", -1)
        val PrefEdit = sharedPreferences.edit()
        usersNumber++
        PrefEdit.putString("username+${usersNumber}", signup_email?.text.toString()).apply()
        PrefEdit.putString("password+${usersNumber}", signup_password1?.text.toString()).apply()
        PrefEdit.putInt("usersNumber", usersNumber).apply()
    }

    private fun regToDatabase() {
        val email = signup_email?.text.toString()
        val name = signup_email?.text.toString().split('@')[0]
        GlobalScope.launch { Repository.getInstance(context).addUser(User(email, name)) }
    }

    private fun regToFirebase() {
        val email = signup_email?.text.toString()
        val name = signup_email?.text.toString()
        FirebaseManager.getInstance(requireContext()).addUser(User(email, name))
    }
}