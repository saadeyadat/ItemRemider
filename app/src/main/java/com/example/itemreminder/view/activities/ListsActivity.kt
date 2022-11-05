package com.example.itemreminder.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.adapters.ListsAdapter
import androidx.fragment.app.Fragment
import com.example.itemreminder.view.fragments.NewListFragment
import com.example.itemreminder.viewModel.ListsViewModel
import com.example.itemreminder.viewModel.UsersViewModel
import kotlinx.android.synthetic.main.lists_activity.*
import kotlin.concurrent.thread

class ListsActivity : AppCompatActivity() {

    private var userString: String = ""
    private val listsViewModel: ListsViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lists_activity)
        val userStr = intent.extras!!.getString("user")
        userString = userStr!!
        listsRecyclerView()
        addListToDB()
    }

    private fun addListToDB() {
        val newListFragment = NewListFragment(userString, this)
        add_list.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.new_list_fragment, newListFragment).commit()
        }
    }

    private fun listsRecyclerView() {
        /*var allLists = mutableListOf<Lists>()
        var allUsers = mutableListOf<String>()
        usersViewModel.usersData.observe(this) {
            for (user in it)
                allUsers.add(user.email)
        }*/

        val adapter = ListsAdapter(this)
        listsRecyclerView?.adapter = adapter
        listsViewModel.listsData.observe(this, Observer {
            /*for (list in it)
                for (user in allUsers)
                    if (list.owner.split("-")[0]==user)
                        allLists.add(list)
                    else if (list.participants != null)
                            if (list.participants!!.contains(user))
                                allLists.add(list)*/
            adapter.setList(it) // it instead of allLists
        })
    }

    //-------------------- logout menu --------------------//

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}