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
import kotlinx.android.synthetic.main.items_activity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyAdapter(private val list: String,
                private val context: Context,
                val updateImage: (Item) -> Unit, // unit is like void
                val displayFruitFragment: (Item) -> Unit): RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var i = 0 // help to scan all dataList and print items to the correct list
    private var dataList = emptyList<Item>()
    fun setList(itemList: List<Item>) {
        this.dataList = itemList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val item_name: TextView
        val item_image: ImageView
        val delete: ImageButton
        init {
            item_name = view.findViewById(R.id.item_name)
            item_image = view.findViewById(R.id.item_image)
            delete = view.findViewById(R.id.delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (dataList.size == i) i = 0
        if (dataList[i++].list == list)
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_adapter, parent, false))
        else
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item_name.text = dataList[position].name

        if (dataList[position].image!!.contains("https://"))
            Glide.with(context).load(dataList[position].image).into(holder.item_image)
        else
            holder.item_image.setImageURI(Uri.parse(dataList[position].image))

        holder.delete.setOnClickListener {
            displayAlert(context, position)
        }

        holder.item_name.setOnClickListener {
            displayFruitFragment(dataList[position])
        }

        holder.item_image.setOnClickListener {
            updateImage(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
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