package com.gabriel_codarcea.mycontacts.api.model

import com.google.gson.annotations.SerializedName

data class RMCharacter(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String
)