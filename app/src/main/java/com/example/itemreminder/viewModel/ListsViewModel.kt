package com.example.itemreminder.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.database.Repository

class ListsViewModel(val app: Application): AndroidViewModel(app) {

    private val repository = Repository.getInstance(app.applicationContext)
    val listsData: LiveData<List<Lists>> = repository.getAllLists()

    fun addLists(lists: Lists) {
        repository.addList(lists)
    }
}