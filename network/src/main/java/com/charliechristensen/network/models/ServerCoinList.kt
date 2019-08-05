package com.charliechristensen.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Suppress("unused")
@JsonClass(generateAdapter = true)
class ServerCoinList(
    @Json(name = "Response") val response: String,
    @Json(name = "Message") val message: String,
    @Json(name = "BaseImageUrl") val baseImageUrl: String,
    @Json(name = "BaseLinkUrl") val baseLinkUrl: String,
    @Json(name = "Type") val type: Int,
    @Json(name = "Data") val data: Map<String, ServerCoinData>
)