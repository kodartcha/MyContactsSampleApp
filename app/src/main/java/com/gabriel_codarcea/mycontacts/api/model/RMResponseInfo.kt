package com.gabriel_codarcea.mycontacts.api.model

import com.google.gson.annotations.SerializedName

data class RMResponseInfo(
    @SerializedName("count") val count: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("prev") val prev: String?
)
