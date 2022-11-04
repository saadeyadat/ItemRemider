package com.example.itemreminder.view.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.itemreminder.model.Item
import com.example.itemreminder.R
import com.example.itemreminder.other.adapters.InfoAdapter
import com.example.itemreminder.model.database.Repository
import kotlinx.android.synthetic.main.item_fragment_info.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ItemFragmentInfo(private val item: Item, context: Context): Fragment(R.layout.item_fragment_info) {

    override fun onResume() {
        super.onResume()

        infoRecyclerView()
        add_button?.setOnClickListener {
            item.info += add_text?.text.toString() + ','
            GlobalScope.launch { Repository.getInstance(context).updateItemInfo(item, item.info) }
            add_text?.setText("")
            infoRecyclerView()
        }

        exit_button2?.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun infoRecyclerView() {
        val infoAdapter = InfoAdapter(item, context)
        infoRecyclerView?.adapter = infoAdapter
    }
}