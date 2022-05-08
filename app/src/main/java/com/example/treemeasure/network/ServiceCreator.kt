package com.example.freshmantwo.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
//    private const val BASE_URL = "http://192.168.38.6:8088/"
//    private const val BASE_URL = "http://192.168.137.1:8088/"
//    private const val BASE_URL = "http://192.168.10.6:8088/"
//    private const val BASE_URL = "http://192.168.1.102:8088/"
    private const val BASE_URL = "http://192.168.1.101:8088/"


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)
}