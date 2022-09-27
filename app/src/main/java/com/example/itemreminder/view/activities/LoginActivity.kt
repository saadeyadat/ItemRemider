package com.example.itemreminder.view.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.itemreminder.R
import com.example.itemreminder.other.register.AppSignin
import com.example.itemreminder.other.service.ItemService
import com.example.itemreminder.view.fragments.SignupFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var googleContent:  ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)
        val serviceIntent = Intent(this, ItemService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        googleContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                content -> googleIntentResult(content)
        }
        sharedPreferences = getSharedPreferences(R.string.app_name.toString(), MODE_PRIVATE)
        //lastSignin()
        signIn()
    }

    private fun lastSignin() {
        var lastSignin = sharedPreferences.getLong("LAST_LOGIN", -1)
        val intent = Intent(this, ItemsActivity::class.java)
        if (lastSignin != -1L && System.currentTimeMillis()-lastSignin < 30000) // 60000 ms = 60 sec
            startActivity(intent)
    }

    private fun signIn() {
        signin_button.setOnClickListener {
            if (AppSignin().checkUser(sharedPreferences, signin_username.text.toString(), signin_password.text.toString())){
                openApp()
                signin_username.setText("")
                signin_password.setText("")
            }
        }
        google_signin.setOnClickListener {
            startLogin()
        }
        signup_text.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.signup_fragment, SignupFragment(sharedPreferences)).commit()
        }
    }

    private fun openApp() {
        var editor = sharedPreferences.edit()
        editor.putLong("LAST_LOGIN", System.currentTimeMillis()).apply()
        val intent = Intent(this, ItemsActivity::class.java)
        startActivity(intent)
    }

    /*-----------------------------------------------------------------*/
    private val firebase = FirebaseAuth.getInstance()
    private fun startLogin() {
        val serviceIntent = Intent(this, ItemService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        val googleOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val client = GoogleSignIn.getClient(this, googleOptions).signInIntent
        googleContent.launch(client)
    }

    private fun googleIntentResult(content: ActivityResult?) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(content?.data)
        task.addOnSuccessListener{ logOrSignFirebase(it) }
            .addOnFailureListener{ displayToast("Please Sign in regular") }
    }

    private fun logOrSignFirebase(googleSignInAccount: GoogleSignInAccount) {
        firebase.fetchSignInMethodsForEmail(googleSignInAccount.email!!)
            .addOnSuccessListener {
                if (it.signInMethods.isNullOrEmpty()) {
                    registerToFirebase(googleSignInAccount)
                }
                else
                    openApp() // googleSignInAccount.displayName.toString()

            }
            .addOnFailureListener { displayToast("Failed on Firebase") }
    }

    private fun registerToFirebase(googleSignInAccount: GoogleSignInAccount) {
        val credetial = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        firebase.signInWithCredential(credetial)
            .addOnSuccessListener { openApp() }
            .addOnFailureListener { displayToast("try later") }
    }

    private fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}