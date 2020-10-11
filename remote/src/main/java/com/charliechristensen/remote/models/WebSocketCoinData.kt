package com.charliechristensen.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WebSocketCoinData(
    @SerialName("TYPE")
    val type: Int? = null,
    @SerialName("MARKET")
    val market: String? = null,
    @SerialName("FROMSYMBOL")
    val fromSymbol: String? = null,
    @SerialName("TOSYMBOL")
    val toSymbol: String? = null,
    @SerialName("FLAGS")
    val flags: Int? = null,
    @SerialName("PRICE")
    val price: Double? = null,
    @SerialName("LASTUPDATE")
    val lastUpdate: Long? = null,
    @SerialName("MEDIAN")
    val median: Double? = null,
    @SerialName("LASTTRADEID")
    val lastTradeId: String? = null,
    @SerialName("VOLUMEDAY")
    val volumeDay: Double? = null,
    @SerialName("VOLUMEDAYTO")
    val volumeDayTo: Double? = null,
    @SerialName("VOLUME24HOUR")
    val volume24Hour: Double? = null,
    @SerialName("VOLUME24HOURTO")
    val volume24HourTo: Double? = null,
    @SerialName("VOLUMEHOUR")
    val volumeHour: Double? = null,
    @SerialName("VOLUMEHOURTO")
    val volumeHourTo: Double? = null,
    @SerialName("TOPTIERVOLUME24HOUR")
    val topTierVolume24Hour: Double? = null,
    @SerialName("TOPTIERVOLUME24HOURTO")
    val topTierVolume24HourTo: Double? = null
)
