package com.example.itemreminder.view.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
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
import kotlinx.android.synthetic.main.activity_main.*
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
        setContentView(R.layout.activity_main)
        recyclerView()
        clickListen()
    }

    private fun clickListen() {
        button.setOnClickListener {
            itemsViewModel.viewModelScope.launch(Dispatchers.IO) {
                itemsViewModel.addItem(Item(editText.text.toString(), String(), String()))
            }
            editText.setText("")
            NotificationsManager.display(this)
        }
    }

    private fun recyclerView() {
        val adapter = MyAdapter(mutableListOf(), this, updateImage()) { displayFruitFragment(it) }
        recyclerView.adapter = adapter
        itemsViewModel.itemsData.observe(this) { adapter.updateView(it) }
    }

    private fun displayFruitFragment(item: Item) {
        val bundle = bundleOf("fruitName" to item.name, "fruitImage" to item.image)
        val fruitFragment = ItemFragment(item, this)
        fruitFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_view, fruitFragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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