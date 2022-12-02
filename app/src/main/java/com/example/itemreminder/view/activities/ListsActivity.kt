package com.example.itemreminder.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.User
import com.example.itemreminder.other.adapters.ListsAdapter
import com.example.itemreminder.other.managers.NotificationsManager
import com.example.itemreminder.other.service.ItemService
import com.example.itemreminder.view.fragments.NewListFragment
import com.example.itemreminder.viewModel.ListsViewModel
import com.example.itemreminder.viewModel.UsersViewModel
import kotlinx.android.synthetic.main.lists_activity.*

class ListsActivity : AppCompatActivity() {

    private val listsViewModel: ListsViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()

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
                }
        }
    }

    private fun addNewList(user: User) {
        val newListFragment = NewListFragment(user, this)
        add_list.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.new_list_fragment, newListFragment).commit()
        }
    }

    private fun listsRecyclerView(user: User) {
        val adapter = ListsAdapter(this)
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            val serviceIntent = Intent(this, ItemService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}