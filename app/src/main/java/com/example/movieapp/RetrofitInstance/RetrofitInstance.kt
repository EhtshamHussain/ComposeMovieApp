package com.example.movieapp.RetrofitInstance

import com.example.movieapp.ApiServices.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance{
    private  val BASE_CASE = "https://api.themoviedb.org/3/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_CASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

}