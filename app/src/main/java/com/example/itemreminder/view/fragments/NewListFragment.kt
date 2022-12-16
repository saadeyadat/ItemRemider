package com.example.itemreminder.view.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.itemreminder.model.Item
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.managers.FirebaseManager
import com.example.itemreminder.other.managers.NotificationsManager
import com.example.itemreminder.viewModel.ListsViewModel
import kotlinx.android.synthetic.main.item_fragment_info.add_button
import kotlinx.android.synthetic.main.item_fragment_info.exit_button2
import kotlinx.android.synthetic.main.new_list_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewListFragment(private val user: User, context: Context): Fragment(R.layout.new_list_fragment) {

    override fun onResume() {
        super.onResume()
        add_button?.setOnClickListener {
            val list = list_name.text.toString()
            GlobalScope.launch {
                Repository.getInstance(context).addUserList(user, list)
                Repository.getInstance(context).addList(Lists("${user.email}-$list", "${user.email}-${user.name}"))
                FirebaseManager.getInstance(requireContext()).addList(Lists("${user.email}-$list", "${user.email}-${user.name}"))
                //NotificationsManager.newList(requireContext(), user.name)
            }
            list_name?.setText("")
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        exit_button2?.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}