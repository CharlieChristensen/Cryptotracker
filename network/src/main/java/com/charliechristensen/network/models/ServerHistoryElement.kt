package com.charliechristensen.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ServerHistoryElement(@Json(name = "time") val time: Long,
                           @Json(name = "close") val close: Double,
                           @Json(name = "high") val high: Double,
                           @Json(name = "low") val low: Double,
                           @Json(name = "open") val open: Double,
                           @Json(name = "volumefrom") val volumeFrom: Double,
                           @Json(name = "volumeto") val volumeTo: Double
)