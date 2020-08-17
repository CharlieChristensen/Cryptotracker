package com.charliechristensen.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebSocketSubscription(
    @Json(name = "action") val action: String,
    @Json(name = "subs") val subs: List<String>
)
