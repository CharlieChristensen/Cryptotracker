package com.charliechristensen.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
data class RemoteCoinList(
    @SerialName("Response") val response: String? = null,
    @SerialName("Message") val message: String? = null,
    @SerialName("BaseImageUrl") val baseImageUrl: String? = null,
    @SerialName("BaseLinkUrl") val baseLinkUrl: String? = null,
    @SerialName("Type") val type: Int? = null,
    @SerialName("Data") val data: Map<String?, RemoteCoinData?>? = null
)
