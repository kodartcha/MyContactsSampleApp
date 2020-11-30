package com.gabriel_codarcea.mycontacts.api

import com.gabriel_codarcea.mycontacts.utils.API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    var apiService: ApiService? = null
    fun getClient(): ApiService? {
        if(apiService == null){
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }
}