package com.charliechristensen.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerCoinData(
    @Json(name = "Id") val id: Int,
    @Json(name = "Url") val url: String,
    @Json(name = "ImageUrl") val imageUrl: String?,
    @Json(name = "Name") val symbol: String,
    @Json(name = "CoinName") val coinName: String,
    @Json(name = "FullName") val fullName: String,
    @Json(name = "Algorithm") val algorithm: String,
    @Json(name = "ProofType") val proofType: String,
    @Json(name = "SortOrder") val sortOrder: Int
)
