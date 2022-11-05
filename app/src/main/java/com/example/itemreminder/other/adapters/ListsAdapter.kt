package com.example.itemreminder.other.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.itemreminder.R
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.view.activities.ItemsActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListsAdapter(private val context: Context): RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

    private var lists = emptyList<Lists>()
    fun setList(list: List<Lists>) {
        this.lists = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val list_name: TextView
        val list_owner: TextView
        val delete: ImageView
        init {
            list_name = view.findViewById(R.id.item_name)
            list_owner = view.findViewById(R.id.list_owner)
            delete = view.findViewById(R.id.delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lists_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_name.text = lists[position].name.split("-")[1]
        holder.list_owner.text = "Owner: " + lists[position].owner.split("-")[0]

        holder.list_name.setOnClickListener {
            val intent = Intent(context, ItemsActivity::class.java)
            intent.putExtra("list", lists[position].name)
            intent.putExtra("user", lists[position].owner)
            context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            displayAlert(context, position)
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    private fun displayAlert(context: Context, position: Int) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle("Delete Item")
        alertBuilder.setMessage("You Will Delete '${lists[position].name.split("-")[0]}':  ")
        alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
        alertBuilder.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
            GlobalScope.launch { Repository.getInstance(context).deleteList(lists[position]) }
            notifyItemRemoved(position)
        }
        alertBuilder.show()
    }
}