package com.example.itemreminder.view.activities

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.example.itemreminder.view.fragments.NewParticipantFragment
import com.example.itemreminder.viewModel.ItemsViewModel
import com.example.itemreminder.viewModel.ListsViewModel
import com.example.itemreminder.viewModel.UsersViewModel
import kotlinx.android.synthetic.main.items_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class ItemsActivity : AppCompatActivity() {

    private val itemsViewModel: ItemsViewModel by viewModels()
    private val listsViewModel: ListsViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.items_activity)
        val listID = intent.extras!!.getString("listID")
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
        user_email.text = list.owner.split("-")[0]
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
        user_image.setOnClickListener { addUserImage(list) }
    }

    private fun addItem(list: Lists) {
        val userEmail = list.owner.split("-")[0]
        val userName = list.owner.split("-")[1]
        val name = edit_text.text.toString()
        val item = Item(userEmail, userName, list.name, name, String(), String())
        itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
            itemsViewModel.addItem(item)
        }
        FirebaseManager.getInstance(this).addItem(Item(userEmail, userName, list.name, name, String(), String()))
        edit_text.setText("")
        NotificationsManager.display(this)
    }

    private fun addParticipant(list: Lists) {
        val allUsersStr = mutableListOf<String>()
        val newParticipantFragment = NewParticipantFragment(list, allUsersStr)
        supportFragmentManager.beginTransaction().replace(R.id.new_participant_fragment, newParticipantFragment).commit()
    }

    private fun addUserImage(list: Lists) {
        usersViewModel.usersData.observe(this) {
            for (user in it)
                if (user.email == list.owner.split("-")[0])
                    currentUser = user
        }
        thread(start = true) { ImagesManager.galleryImage(userContent) }
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
        val adapter = ItemsAdapter(list.name, this, updateImage()) { displayItemFragment(it) }
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
        displayAlert(this)
    }

    private var currentUser: User? = null
    private val userContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        user_image.setImageURI(uri)
        ImagesManager.userImageFromGallery(uri!!, this, currentUser!!)
    }

    private fun displayAlert(context: Context) {
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
}