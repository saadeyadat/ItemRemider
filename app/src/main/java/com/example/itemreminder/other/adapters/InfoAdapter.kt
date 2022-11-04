package com.example.itemreminder.other.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.itemreminder.model.Item
import com.example.itemreminder.R
import com.example.itemreminder.model.database.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InfoAdapter(private val item: Item, private val context: Context?): RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    private var infoList: MutableList<String> = item.info.split(',').toMutableList() // convert info string to list to show it via recyclerview
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView
        val deleteButton: ImageButton
        init {
            textView = view.findViewById(R.id.info_line)
            deleteButton = view.findViewById(R.id.delete_line)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.info_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = infoList[position]
        holder.deleteButton.setOnClickListener {
            displayAlert(context!!, position, holder)
        }
    }

    override fun getItemCount(): Int {
        return infoList.size-1
    }

    private fun displayAlert(context: Context, position: Int, holder: ViewHolder) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle("Delete Note")
        alertBuilder.setMessage("You Will Delete '${infoList[position]}':  ")
        alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
        alertBuilder.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
            infoList.removeAt(holder.layoutPosition)
            item.info = infoList.joinToString(",") // covert back the list to string to save it in the database
            GlobalScope.launch { Repository.getInstance(context).updateItemInfo(item, item.info) }
            notifyItemRemoved(position)
        }
        alertBuilder.show()
    }
}