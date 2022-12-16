package com.example.itemreminder.view.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.viewModelScope
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.adapters.ListsAdapter
import com.example.itemreminder.other.managers.ImagesManager
import com.example.itemreminder.view.fragments.NewListFragment
import com.example.itemreminder.viewModel.ListsViewModel
import com.example.itemreminder.viewModel.UsersViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.items_activity.*
import kotlinx.android.synthetic.main.lists_activity.*
import kotlinx.android.synthetic.main.menu_header.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread

class ListsActivity : AppCompatActivity() {

    private val listsViewModel: ListsViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    lateinit var toggle: ActionBarDrawerToggle
    var allowCamera = false
    var allowResult = false
    var currentActivity = this
    private var currentUser: User? = null
    private val userContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        menu_user_image.setImageURI(uri)
        ImagesManager.userImageFromGallery(uri!!, this, currentUser!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lists_activity)
        val userEmail = intent.extras!!.getString("userEmail")
        setUser(userEmail!!)
    }

    private fun setUser(userEmail: String) {
        usersViewModel.usersData.observe(this) {
            for (user in it)
                if (user.email == userEmail) {
                    listsRecyclerView(user)
                    addNewList(user)
                    setMenuBar(user)
                }
        }
    }

    private fun setMenuBar(user: User) {
        val drawer: DrawerLayout = findViewById(R.id.lists_drawer_layout)
        val menu: NavigationView = findViewById(R.id.menu)

        toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        menu_user_image.setImageURI(Uri.parse(user.image))
        menu_user_name.text = user.name
        menu_user_email.text = user.email

        menu_user_image.setOnClickListener { addUserImage(user) }

        menu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> Toast.makeText(applicationContext, "Home", Toast.LENGTH_SHORT).show()
                R.id.about -> Toast.makeText(applicationContext, "About", Toast.LENGTH_SHORT).show()
                R.id.logout -> Toast.makeText(applicationContext, "Logout", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun addNewList(user: User) {
        val newListFragment = NewListFragment(user, this)
        add_list.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.new_list_fragment, newListFragment).commit()
        }
    }

    private fun addUserImage(user: User) {

        currentUser = user
        if (ActivityCompat.checkSelfPermission(currentActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(currentActivity, arrayOf(android.Manifest.permission.CAMERA), 111)
            allowResult = true
        }
        else {
            allowCamera = true
            allowResult = true
        }

        if (allowResult) {
            if (allowCamera)
                cameraAlert(this)
            else
                userImageAlert(this)
        }
        else
            onlyOwnerAllowedAlert(this)
    }


    //-------------------- lists RecyclerView --------------------//

    private fun listsRecyclerView(user: User) {
        val adapter = ListsAdapter(this, user)
        listsRecyclerView?.adapter = adapter
        listsViewModel.listsData.observe(this) { adapter.setList(setUserLists(it, user)) }
    }

    private fun setUserLists(allLists: List<Lists>, user: User): MutableList<Lists> {
        var userLists = mutableListOf<Lists>()
        for (list in allLists) {
            var flag = 0
            for (userList in userLists)
                if (userList.name == list.name)
                    flag++
            if (list.owner.split("-")[0] == user.email && flag == 0)
                userLists.add(list)
            else if (list.participants!!.isNotEmpty())
                    if (list.participants!!.contains(user.email) && flag == 0)
                        userLists.add(list)
        }
        return userLists
    }



    //-------------------- logout menu --------------------//

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            val serviceIntent = Intent(this, ItemService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /* ---------------- Camera Images Update ---------------- */

    private fun cameraAlert(context: Context) {
        usersViewModel.viewModelScope.launch(Dispatchers.Main) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setTitle("Change Image")
            alertBuilder.setMessage("Select Image Source:  ")
            alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
            alertBuilder.setNegativeButton("Camera") { dialogInterface: DialogInterface, i: Int ->
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 101)
            }
            alertBuilder.setPositiveButton("Gallery") { dialogInterface: DialogInterface, i: Int ->
                usersViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.galleryImage(userContent)
                }
            }
            alertBuilder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            var pic = data?.getParcelableExtra<Bitmap>("data")
            val bytes = ByteArrayOutputStream()
            if (pic != null) {
                pic!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(contentResolver, pic, "val", null)
                val uri = Uri.parse(path)
                menu_user_image.setImageURI(uri)
                thread(start = true) { Repository.getInstance(this).updateUserImage(currentUser!!, uri.toString()) }
            }
        }
    }

    private fun userImageAlert(context: Context) {
        usersViewModel.viewModelScope.launch(Dispatchers.Main) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setTitle("Change Image")
            alertBuilder.setMessage("Select Image Source:  ")
            alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
            alertBuilder.setPositiveButton("Gallery") { dialogInterface: DialogInterface, i: Int ->
                usersViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.galleryImage(userContent)
                }
            }
            alertBuilder.show()
        }
    }

    private fun onlyOwnerAllowedAlert(context: Context) {
        usersViewModel.viewModelScope.launch(Dispatchers.Main) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setTitle("Alert")
            alertBuilder.setMessage("Only List Owner Can Edit This Field.")
            alertBuilder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int -> }
            alertBuilder.show()
        }
    }
}