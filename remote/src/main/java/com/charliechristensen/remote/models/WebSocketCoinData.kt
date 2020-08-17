package com.charliechristensen.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class WebSocketCoinData(
    @Json(name = "TYPE")
    val type: Int? = null,
    @Json(name = "MARKET")
    val market: String? = null,
    @Json(name = "FROMSYMBOL")
    val fromSymbol: String? = null,
    @Json(name = "TOSYMBOL")
    val toSymbol: String? = null,
    @Json(name = "FLAGS")
    val flags: Int? = null,
    @Json(name = "PRICE")
    val price: Double? = null,
    @Json(name = "LASTUPDATE")
    val lastUpdate: Long? = null,
    @Json(name = "MEDIAN")
    val median: Double? = null,
    @Json(name = "LASTTRADEID")
    val lastTradeId: String? = null,
    @Json(name = "VOLUMEDAY")
    val volumeDay: Double? = null,
    @Json(name = "VOLUMEDAYTO")
    val volumeDayTo: Double? = null,
    @Json(name = "VOLUME24HOUR")
    val volume24Hour: Double? = null,
    @Json(name = "VOLUME24HOURTO")
    val volume24HourTo: Double? = null,
    @Json(name = "VOLUMEHOUR")
    val volumeHour: Double? = null,
    @Json(name = "VOLUMEHOURTO")
    val volumeHourTo: Double? = null,
    @Json(name = "TOPTIERVOLUME24HOUR")
    val topTierVolume24Hour: Double? = null,
    @Json(name = "TOPTIERVOLUME24HOURTO")
    val topTierVolume24HourTo: Double? = null
)
