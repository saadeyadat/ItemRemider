package com.example.itemreminder.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.itemreminder.other.service.ItemService
import com.example.itemreminder.R
import com.example.itemreminder.view.fragments.LoginFragment
import com.example.itemreminder.view.fragments.SignupFragment
import kotlinx.android.synthetic.main.activity_register.*
import android.content.SharedPreferences as SharedPreferences

class RegisterActivity : AppCompatActivity() {

    private var isLoginFragment = true
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //ImagesManager.apiImages()
        val serviceIntent = Intent(this, ItemService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        sharedPreferences = getSharedPreferences(R.string.app_name.toString(), MODE_PRIVATE)
        lastLogin()
        login_signup_text.setOnClickListener { checkPage() }
    }

    private fun lastLogin() {
        var lastLogin = sharedPreferences.getLong("LAST_LOGIN", -1)
        val intent = Intent(this, ItemsActivity::class.java)
        if (lastLogin != -1L && System.currentTimeMillis()-lastLogin < 30000) // 60000 ms = 60 sec
            startActivity(intent)
    }

    private fun checkPage() {
        if (isLoginFragment)
            logIn()
        else
            signUp()
    }

    private fun logIn() {
        val loginFragment = LoginFragment()
        login_signup_text.text = "Click to Sign-up"
        supportFragmentManager.beginTransaction().replace(R.id.register_fragment, loginFragment).commit()
        if (isLoginFragment)
            register_button.setOnClickListener { loginFragment.startLogin(this, sharedPreferences) }
        isLoginFragment = false
    }

    private fun signUp() {
        val signupFragment = SignupFragment()
        login_signup_text.text = "Click to Login"
        supportFragmentManager.beginTransaction().replace(R.id.register_fragment, signupFragment).commit()
        if (!isLoginFragment)
            register_button.setOnClickListener { signupFragment.createUser(sharedPreferences) }
        isLoginFragment = true
    }
}