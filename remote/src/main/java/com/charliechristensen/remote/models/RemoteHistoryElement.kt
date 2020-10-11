package com.charliechristensen.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteHistoryElement(
    @SerialName("time") val time: Long? = null,
    @SerialName("close") val close: Double? = null,
    @SerialName("high") val high: Double? = null,
    @SerialName("low") val low: Double? = null,
    @SerialName("open") val open: Double? = null,
    @SerialName("volumefrom") val volumeFrom: Double? = null,
    @SerialName("volumeto") val volumeTo: Double? = null
)
