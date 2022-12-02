package com.example.itemreminder.view.fragments

import androidx.fragment.app.Fragment
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.managers.FirebaseManager
import kotlinx.android.synthetic.main.delete_participant_fragment.*
import kotlinx.android.synthetic.main.item_fragment_info.add_button
import kotlinx.android.synthetic.main.item_fragment_info.exit_button2
import kotlinx.android.synthetic.main.new_participant_fragment.*
import kotlinx.android.synthetic.main.new_participant_fragment.participant_name
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DeleteParticipantFragment(private val list: Lists): Fragment(R.layout.delete_participant_fragment) {

    override fun onResume() {
        super.onResume()
        delete_button?.setOnClickListener {
            var participant = participant_name.text.toString()
            var newParticipants = ""
            if (list.participants!!.isNotEmpty()) {
                var allParticipants = list.participants!!.split("-")
                if (list.participants!!.contains(participant))
                    for (user in allParticipants)
                        if (user != participant)
                            newParticipants += "$user-"
            }
            GlobalScope.launch {
                Repository.getInstance(context).updateParticipants(list, newParticipants)
                FirebaseManager.getInstance(requireContext()).updateListParticipants(list)
            }
            participant_name?.setText("")
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        exit_button2?.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}