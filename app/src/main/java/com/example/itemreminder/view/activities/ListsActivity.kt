package com.example.itemreminder.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.adapters.ListsAdapter
import com.example.itemreminder.viewModel.ListsViewModel
import kotlinx.android.synthetic.main.items_activity.*
import kotlinx.android.synthetic.main.lists_activity.*
import kotlinx.android.synthetic.main.lists_activity.editText
import kotlin.concurrent.thread

class ListsActivity : AppCompatActivity() {

    private val listsViewModel: ListsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lists_activity)
        val email = intent.extras!!.getString("email")
        listsRecyclerView()
        addListToDB(email!!)
    }

    private fun addListToDB(email: String) {
        add_list.setOnClickListener {
            val listName = editText.text.toString()
            val listID = "$email-$listName"
            thread(start = true) { Repository.getInstance(this).addList(Lists(listID)) }
            editText.setText("")
        }
    }

    private fun listsRecyclerView() {
        val adapter = ListsAdapter(this)
        listsRecyclerView?.adapter = adapter
        listsViewModel.listsData.observe(this, Observer {
            adapter.setList(it)
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