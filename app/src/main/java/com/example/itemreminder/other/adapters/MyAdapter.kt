package com.example.itemreminder.other.adapters

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyAdapter(private val dataList: MutableList<Item>,
                private val context: Context,
                val updateImage: (Item) -> Unit,
                val displayFruitFragment: (Item) -> Unit): RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView
        val imageView: ImageView
        val delete: ImageButton
        init {
            textView = view.findViewById(R.id.text_view)
            imageView = view.findViewById(R.id.image)
            delete = view.findViewById(R.id.delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataList[position].name

        if (dataList[position].image!!.contains("https://"))
            Glide.with(context).load(dataList[position].image).into(holder.imageView)
        else
            holder.imageView.setImageURI(Uri.parse(dataList[position].image))

        holder.delete.setOnClickListener {
            displayAlert(context, position)
        }

        holder.textView.setOnClickListener {
            displayFruitFragment(dataList[position])
        }

        holder.imageView.setOnClickListener {
            updateImage(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateView(itemList: List<Item>) {
        dataList.clear()
        dataList.addAll(itemList)
        notifyDataSetChanged()
    }

    private fun displayAlert(context: Context, position: Int) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle("Delete Item")
        alertBuilder.setMessage("You Will Delete '${dataList[position].name}':  ")
        alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
        alertBuilder.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
            GlobalScope.launch { Repository.getInstance(context).deleteItem(dataList[position]) }
            notifyItemRemoved(position)
        }
        alertBuilder.show()
    }
}