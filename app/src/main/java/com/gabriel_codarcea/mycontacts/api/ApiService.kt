package com.gabriel_codarcea.mycontacts.api

import com.gabriel_codarcea.mycontacts.api.model.RMResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("character/")
    fun getCharacters(@Query("page") page: Int): Call<RMResponse>
}
