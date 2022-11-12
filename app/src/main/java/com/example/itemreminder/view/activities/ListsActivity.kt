package com.example.itemreminder.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.itemreminder.R
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.Lists
import com.example.itemreminder.other.adapters.ListsAdapter
import com.example.itemreminder.view.fragments.NewListFragment
import com.example.itemreminder.viewModel.ItemsViewModel
import com.example.itemreminder.viewModel.ListsViewModel
import kotlinx.android.synthetic.main.lists_activity.*

class ListsActivity : AppCompatActivity() {

    private var user: String = ""
    private val listsViewModel: ListsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lists_activity)
        val userStr = intent.extras!!.getString("user")
        user = userStr!!
        listsRecyclerView()
        addListToDB()
    }

    private fun addListToDB() {
        val newListFragment = NewListFragment(user, this)
        add_list.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.new_list_fragment, newListFragment).commit()
        }
    }

    private fun listsRecyclerView() {
        val adapter = ListsAdapter(this)
        listsRecyclerView?.adapter = adapter
        adapter.setList(setUserLists())
    }

    private fun setUserLists(): MutableList<Lists> {
        var userLists = mutableListOf<Lists>()
        listsViewModel.listsData.observe(this, Observer {
            for (list in it) {
                var flag = 0
                for (userList in userLists)
                    if (userList.name == list.name)
                        flag++
                if (list.owner == user && flag == 0)
                    userLists.add(list)
                else if (list.participants!!.isNotEmpty()) {
                    val listArr = list.participants!!.split("-")
                    val userEmail = user.split("-")[0].split("_")[0]
                    if (listArr.contains(userEmail) && flag == 0)
                        userLists.add(list)
                }
            }
        })
        return userLists
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