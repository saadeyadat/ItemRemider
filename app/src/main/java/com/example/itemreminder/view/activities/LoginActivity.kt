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
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.managers.FirebaseManager
import com.example.itemreminder.other.managers.SharedPrefManager
import com.example.itemreminder.other.register.AppSignin
import com.example.itemreminder.other.service.ItemService
import com.example.itemreminder.view.fragments.SignupFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var googleContent:  ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        val serviceIntent = Intent(this, ItemService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        googleContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                content -> googleIntentResult(content)
        }
        sharedPreferences = getSharedPreferences(R.string.app_name.toString(), MODE_PRIVATE)
        lastSigning()
        signIn()
    }

    private fun lastSigning() {
        var lastSignin = sharedPreferences.getLong("LAST_LOGIN", -1)
        val intent = Intent(this, ItemsActivity::class.java)
        if (lastSignin != -1L && System.currentTimeMillis()-lastSignin < 60000) // 60000 ms = 60 sec
            startActivity(intent)
    }

    private fun signIn() {
        signin_button.setOnClickListener {
            if (AppSignin(this).checkUser(sharedPreferences, signin_username.text.toString(), signin_password.text.toString())){
                openApp(signin_username.text.toString()+"@gmail.com")
                signin_username.setText("")
                signin_password.setText("")
            }
        }
        google_signin.setOnClickListener {
            startLogin()
        }
        signup_text.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.signup_fragment, SignupFragment(sharedPreferences, this)).commit()
        }
    }

    private fun openApp(email: String) {
        var editor = sharedPreferences.edit()
        editor.putLong("LAST_LOGIN", System.currentTimeMillis()).apply()
        val intent = Intent(this, ListsActivity::class.java)
        intent.putExtra("userEmail", email)
        startActivity(intent)
    }

    /*--------------------------FireBase---------------------------*/

    private val firebase = FirebaseAuth.getInstance()
    private fun startLogin() {
        val serviceIntent = Intent(this, ItemService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        val googleOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        val client = GoogleSignIn.getClient(this, googleOptions).signInIntent
        googleContent.launch(client)
    }

    private fun googleIntentResult(content: ActivityResult?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(content?.data)
        task.addOnSuccessListener{ checkUserExist(it) }
            .addOnFailureListener{ displayToast("Please Sign in regular") }
    }

    private fun checkUserExist(googleSignInAccount: GoogleSignInAccount) {
        firebase.fetchSignInMethodsForEmail(googleSignInAccount.email!!)
            .addOnSuccessListener {
                if (it.signInMethods.isNullOrEmpty()) { // if user is not exist in the firebase, register it in all the databases.
                    regToSharedPref(googleSignInAccount)
                    regToDatabase(googleSignInAccount)
                    regToFirebase(googleSignInAccount)
                }
                else // if user exist in firebase you can open the app.
                    openApp(googleSignInAccount.email.toString())
            }
            .addOnFailureListener { displayToast("Failed on Firebase") }
    }


    private fun regToFirebase(googleSignInAccount: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        firebase.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = User(googleSignInAccount.email.toString(), googleSignInAccount.givenName.toString())
                FirebaseManager.getInstance(this).addUser(user)
                openApp(googleSignInAccount.email.toString())
            }
            .addOnFailureListener { displayToast("try later") }
    }

    private fun regToSharedPref(googleSignInAccount: GoogleSignInAccount) {
        val user = User(email = googleSignInAccount.email.toString(), name = googleSignInAccount.givenName.toString())
        SharedPrefManager.getInstance(this).setUser(user)
    }

    private fun regToDatabase(googleSignInAccount: GoogleSignInAccount) {
        val name = googleSignInAccount.givenName.toString()
        val email = googleSignInAccount.email.toString()
        val context = this
        GlobalScope.launch { Repository.getInstance(context).addUser(User(email, name)) }
    }

    private fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}