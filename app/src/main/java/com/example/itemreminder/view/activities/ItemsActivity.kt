package com.example.itemreminder.view.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.example.itemreminder.other.managers.ImagesManager
import com.example.itemreminder.R
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.adapters.ItemsAdapter
import com.example.itemreminder.other.adapters.ParticipantsAdapter
import com.example.itemreminder.other.managers.FirebaseManager
import com.example.itemreminder.view.fragments.ItemFragment
import com.example.itemreminder.other.managers.NotificationsManager
import com.example.itemreminder.view.fragments.DeleteParticipantFragment
import com.example.itemreminder.view.fragments.NewParticipantFragment
import com.example.itemreminder.viewModel.ItemsViewModel
import com.example.itemreminder.viewModel.ListsViewModel
import com.example.itemreminder.viewModel.UsersViewModel
import kotlinx.android.synthetic.main.items_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread

class ItemsActivity : AppCompatActivity() {

    var allowCamera = false
    var allowResult = false
    var currentActivity = this
    var currentUserEmail = ""
    private val itemsViewModel: ItemsViewModel by viewModels()
    private val listsViewModel: ListsViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.items_activity)
        val listID = intent.extras!!.getString("listID")
        val userEmail = intent.extras!!.getString("userEmail")
        currentUserEmail = userEmail!!
        setList(listID!!)
    }

    private fun setList(listID: String){
        listsViewModel.listsData.observe(this) {
            for (list in it)
                if (list.name == listID) {
                    displayInfo(list)
                    clickListen(list)
                    itemsRecyclerView(list)
                    participantsRecyclerView(list.participants!!)
                }
        }
    }

    private fun displayInfo(list: Lists) {
        user_name.text = list.owner.split("-")[1]
        list_name.text = list.name.split('-')[1]
        usersViewModel.usersData.observe(this) {
            for (user in it)
                if (user.email == list.owner.split("-")[0])
                    if (user.image!!.isNotEmpty())
                        user_image.setImageURI(Uri.parse(user.image))
        }
    }

    private fun clickListen(list: Lists) {
        add_item.setOnClickListener { addItem(list) }
        add_participant.setOnClickListener { addParticipant(list) }
        delete_participant.setOnClickListener { deleteParticipant(list) }
        user_image.setOnClickListener { addUserImage(list) }
    }

    private fun addItem(list: Lists) {
        val userEmail = list.owner.split("-")[0]
        val userName = list.owner.split("-")[1]
        val name = edit_text.text.toString()
        val item = Item(userEmail, userName, list.name, name, String(), String())
        if (currentUserEmail == list.owner.split("-")[0]) {
            itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
                itemsViewModel.addItem(item)
            }
            FirebaseManager.getInstance(this)
                .addItem(Item(userEmail, userName, list.name, name, String(), String()))
            edit_text.setText("")
            NotificationsManager.newItem(this, list.name.split("-")[1])
        }
        else
            onlyOwnerAllowedAlert(this)
    }

    private fun addParticipant(list: Lists) {
        val allUsers = mutableListOf<String>()
        val newParticipantFragment = NewParticipantFragment(list, allUsers)
        usersViewModel.usersData.observe(this) {
            for (user in it )
                allUsers.add(user.email)
        }
        if (currentUserEmail == list.owner.split("-")[0])
            supportFragmentManager.beginTransaction().replace(R.id.new_participant_fragment, newParticipantFragment).commit()
        else
            onlyOwnerAllowedAlert(this)
    }

    private fun deleteParticipant(list: Lists) {
        val deleteParticipantFragment = DeleteParticipantFragment(list)
        if (currentUserEmail == list.owner.split("-")[0])
            supportFragmentManager.beginTransaction().replace(R.id.delete_participant_fragment, deleteParticipantFragment).commit()
        else
            onlyOwnerAllowedAlert(this)
    }

    private fun addUserImage(list: Lists) {
        usersViewModel.usersData.observe(this) {
            for (user in it)
                if (user.email == list.owner.split("-")[0])
                    currentUser = user
        }

        if (ActivityCompat.checkSelfPermission(currentActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(currentActivity, arrayOf(android.Manifest.permission.CAMERA), 111)
            allowResult = true
        }
        else {
            allowCamera = true
            allowResult = true
        }

        if (currentUserEmail == list.owner.split("-")[0]) {
            if (allowResult) {
                if (allowCamera)
                    cameraAlert(this)
                else
                    userImageAlert(this)
            }
        }
        else
                onlyOwnerAllowedAlert(this)
    }


    /* ---------------- RecyclerViews ---------------- */

    private fun participantsRecyclerView(participants: String) {
        usersViewModel.usersData.observe(this) {
            if (participants.isNotEmpty()) {
                val participantsList = mutableListOf<String>()
                for (participant in participants.split("-"))
                    if (participant.length > 2)
                        participantsList.add(participant)
                val adapter = ParticipantsAdapter(participantsList, it)
                participants_recyclerView.adapter = adapter
            }
        }
    }

    private fun itemsRecyclerView(list: Lists) {
        val adapter = ItemsAdapter(list, currentUserEmail, this, updateImage()) { displayItemFragment(it) }
        item_recyclerView.adapter = adapter
        itemsViewModel.itemsData.observe(this) { adapter.setList(it) }
    }

    private fun displayItemFragment(item: Item) {
        val bundle = bundleOf("fruitName" to item.name, "fruitImage" to item.image)
        val itemFragment = ItemFragment(item, this)
        itemFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_view, itemFragment).commit()
    }


    /* ---------------- Gallery Images Update ---------------- */

    private var currentItem: Item? = null
    private val itemContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        ImagesManager.itemImageFromGallery(uri!!, this, currentItem!!)
    }
    private fun updateImage(): (item: Item) -> Unit = {
        currentItem = it
        itemImageAlert(this)
    }

    private var currentUser: User? = null
    private val userContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        user_image.setImageURI(uri)
        ImagesManager.userImageFromGallery(uri!!, this, currentUser!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            allowCamera = true
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
                user_image.setImageURI(uri)
                thread(start = true) { Repository.getInstance(this).updateUserImage(currentUser!!, uri.toString()) }
            }
        }
    }


    /* ---------------- Alerts ---------------- */

    private fun itemImageAlert(context: Context) {
        itemsViewModel.viewModelScope.launch(Dispatchers.Main) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setTitle("Change Image")
            alertBuilder.setMessage("Select Image Source:  ")
            alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
            alertBuilder.setNegativeButton("Network") { dialogInterface: DialogInterface, i: Int ->
                itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.apiImages(context, currentItem!!)
                }
            }
            alertBuilder.setPositiveButton("Gallery") { dialogInterface: DialogInterface, i: Int ->
                itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.galleryImage(itemContent)
                }
            }
            alertBuilder.show()
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