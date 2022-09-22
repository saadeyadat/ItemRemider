package com.example.itemreminder.other.api

import com.example.itemreminder.other.ApiResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("?key=30069725-1a4aa4d914f2f1fc16b0acf53&q=fruit&image_type=photo&pretty=true")
    fun getImage(@Query("q") itemName: String): Call<ApiResponse>

    companion object {
        const val BASE_URL = "https://pixabay.com/api/"
        fun create(): ApiInterface {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL).build()
                .create(ApiInterface::class.java)
        }
    }
}