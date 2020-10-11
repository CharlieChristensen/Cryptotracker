package com.charliechristensen.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCoinData(
    @SerialName("Id") val id: Int? = null,
    @SerialName("Url") val url: String? = null,
    @SerialName("ImageUrl") val imageUrl: String? = null,
    @SerialName("Name") val symbol: String? = null,
    @SerialName("CoinName") val coinName: String? = null,
    @SerialName("FullName") val fullName: String? = null,
    @SerialName("Algorithm") val algorithm: String? = null,
    @SerialName("ProofType") val proofType: String? = null,
    @SerialName("SortOrder") val sortOrder: Int? = null
)
