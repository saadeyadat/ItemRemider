package com.example.itemreminder.view.fragments

import androidx.fragment.app.Fragment
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.database.Repository
import kotlinx.android.synthetic.main.item_fragment_info.add_button
import kotlinx.android.synthetic.main.item_fragment_info.exit_button2
import kotlinx.android.synthetic.main.new_participant_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewParticipantFragment(private val list: Lists, private val allUsers: MutableList<String>): Fragment(R.layout.new_participant_fragment) {

    override fun onResume() {
        super.onResume()
        add_button?.setOnClickListener {
            val participant = participant_name.text.toString()
            if (allUsers.contains(participant))
                GlobalScope.launch {
                    Repository.getInstance(context).addParticipant(list, participant)
                }
            participant_name?.setText("")
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        exit_button2?.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}