package com.example.itemreminder.view.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.example.itemreminder.other.managers.ImagesManager
import com.example.itemreminder.R
import com.example.itemreminder.model.Item
import com.example.itemreminder.other.adapters.MyAdapter
import com.example.itemreminder.view.fragments.ItemFragment
import com.example.itemreminder.other.managers.NotificationsManager
import com.example.itemreminder.viewModel.ItemsViewModel
import kotlinx.android.synthetic.main.items_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemsActivity : AppCompatActivity() {

    private val itemsViewModel: ItemsViewModel by viewModels()
    private var currentItem: Item? = null
    val content = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data
            ImagesManager.getImageResultFromGallery(uri!!, this, currentItem!!)
        }

    private fun updateImage(): (item: Item) -> Unit = { fruit ->
        currentItem = fruit
        displayAlert(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.items_activity)
        val list = intent.extras!!.getString("list")
        val userStr = intent.extras!!.getString("user")
        user_name.text = userStr!!.split("-")[1]
        user_email.text = userStr!!.split("-")[0]
        item_name.text = list!!.split('-')[1]
        recyclerView(list!!)
        clickListen(list!!, userStr!!)
    }

    private fun clickListen(list: String, userStr: String) {
        val userEmail = userStr!!.split("-")[0]
        val userName = userStr!!.split("-")[1]
        item_name.text = list.split('-')[1]
        add_item.setOnClickListener {
            val name = edit_text.text.toString()
            itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
                itemsViewModel.addItem(Item(userEmail, userName, list, name, String(), String()))
            }
            edit_text.setText("")
            NotificationsManager.display(this)
        }
    }

    private fun recyclerView(list: String) {
        val adapter = MyAdapter(list, this, updateImage()) { displayItemFragment(it) }
        item_recyclerView.adapter = adapter
        itemsViewModel.itemsData.observe(this) { adapter.setList(it) }
    }

    private fun displayItemFragment(item: Item) {
        val bundle = bundleOf("fruitName" to item.name, "fruitImage" to item.image)
        val itemFragment = ItemFragment(item, this)
        itemFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_view, itemFragment).commit()
    }

    private fun displayAlert(context: Context) {
        itemsViewModel.viewModelScope.launch(Dispatchers.Main) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setTitle("Change Image")
            alertBuilder.setMessage("Select Image Source:  ")
            alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
            alertBuilder.setNegativeButton("Network") { dialogInterface: DialogInterface, i: Int ->
                itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.apiImages(context, currentItem!!)
                }
            }
            alertBuilder.setPositiveButton("Gallery") { dialogInterface: DialogInterface, i: Int ->
                itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.galleryImage(content)
                }
            }
            alertBuilder.show()
        }
    }
}