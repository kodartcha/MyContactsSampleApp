package com.gabriel_codarcea.mycontacts.api.model

import com.google.gson.annotations.SerializedName

data class RMResponse(
    @SerializedName("info") val info: RMResponseInfo,
    @SerializedName("results") val results: List<RMCharacter>
)