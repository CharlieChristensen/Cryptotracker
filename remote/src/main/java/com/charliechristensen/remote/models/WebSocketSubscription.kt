package com.charliechristensen.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketSubscription(
    val action: String,
    val subs: List<String>
)
