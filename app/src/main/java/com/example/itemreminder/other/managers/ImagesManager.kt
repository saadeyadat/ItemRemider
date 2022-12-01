package com.example.itemreminder.other.managers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.itemreminder.other.api.ApiInterface
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.User
import com.example.itemreminder.model.database.Repository
import com.example.itemreminder.other.ApiResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

object ImagesManager {

    fun galleryImage(content: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        content.launch(intent)
    }

    fun userImageFromGallery(uri: Uri, context: Context, user: User) {
        context.contentResolver.takePersistableUriPermission(uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        GlobalScope.launch {
            Repository.getInstance(context).updateUserImage(user, uri.toString())
            Repository.getInstance(context).addUserImageToFB(uri, user)
        }
    }

    fun itemImageFromGallery(uri: Uri, context: Context, item: Item) {
        context.contentResolver.takePersistableUriPermission(uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        GlobalScope.launch { Repository.getInstance(context).updateItemImage(item, uri.toString()) }
    }

    fun apiImages(context: Context, item: Item) {
        val retroData = ApiInterface.create().getImage(item.name)
        retroData.enqueue(object : retrofit2.Callback<ApiResponse?> {
            override fun onResponse(call: Call<ApiResponse?>, response: Response<ApiResponse?>) {
                val i = (0..response.body()!!.imagesList.size).random()
                GlobalScope.launch {
                    Repository.getInstance(context).updateItemImage(item, response.body()!!.imagesList[i].image)
                    FirebaseManager.getInstance(context).updateItemImage(item)
                }
            }

            override fun onFailure(call: Call<ApiResponse?>, t: Throwable) {}
        })
    }
}