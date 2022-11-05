package com.example.itemreminder.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository

class UsersViewModel(val app: Application): AndroidViewModel(app) {

    private val repository = Repository.getInstance(app.applicationContext)
    val usersData: LiveData<List<User>> = repository.getAllUsers()

    fun addUser(user: User) {
        repository.addUser(user)
    }
}