package com.example.itemreminder.other.managers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import com.example.itemreminder.other.api.ApiInterface
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.ApiResponse
import retrofit2.Call
import retrofit2.Response
import kotlin.concurrent.thread

object ImagesManager {

    fun displayAlert(context: Context, content: ActivityResultLauncher<Intent>, item: Item) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle("Change Image")
        alertBuilder.setMessage("Select Image Source:  ")
        alertBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
        alertBuilder.setNegativeButton("Network") { dialogInterface: DialogInterface, i: Int -> apiImages(context, item) }
        alertBuilder.setPositiveButton("Gallery") { dialogInterface: DialogInterface, i: Int -> galleryImage(content) }
        alertBuilder.show()
    }

    private fun galleryImage(content: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        content.launch(intent)
    }

    fun getImageResultFromGallery(uri: Uri, context: Context, item: Item) {
        context.contentResolver.takePersistableUriPermission(uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addImage(context, item, uri!!)
    }

    private fun addImage(context: Context, item: Item, image: Uri) {
        thread(start = true) { Repository.getInstance(context).updateItemImage(item, image.toString()) }
    }

    fun apiImages(context: Context, item: Item) {
        val retroData = ApiInterface.create().getImage(item.name)
        retroData.enqueue(object : retrofit2.Callback<ApiResponse?> {
            override fun onResponse(call: Call<ApiResponse?>, response: Response<ApiResponse?>) {
                val i = (0..response.body()!!.imagesList.size).random()
                thread(start = true) {
                    Repository.getInstance(context).updateItemImage(item, response.body()!!.imagesList[i].image)
                }
            }

            override fun onFailure(call: Call<ApiResponse?>, t: Throwable) {}
        })
    }
}