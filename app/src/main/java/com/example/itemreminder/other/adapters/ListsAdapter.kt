package com.example.itemreminder.other.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.view.activities.ItemsActivity

class ListsAdapter(private val context: Context): RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

    private var lists = emptyList<Lists>()
    fun setList(list: List<Lists>) {
        this.lists = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView
        val imageView: ImageView
        init {
            textView = view.findViewById(R.id.text_view)
            imageView = view.findViewById(R.id.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lists_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val onlyListName = lists[position].name.split("-")
        holder.textView.text = onlyListName[1]
        holder.imageView.setOnClickListener {
            val intent = Intent(context, ItemsActivity::class.java)
            intent.putExtra("list", lists[position].name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

}