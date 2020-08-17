@file:Suppress("unused")

package com.charliechristensen.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteTopListCoinData(
    @Json(name = "RAW") val rawData: Map<String?, Map<String?, RemoteCoinPriceRawData?>>?,
    @Json(name = "DISPLAY") val displayData: Map<String?, Map<String?, ServerCoinPriceDisplayData?>>?
)

@SuppressWarnings("SpellCheckingInspection")
@JsonClass(generateAdapter = true)
data class RemoteCoinPriceData(
    @Json(name = "RAW") val rawData: Map<String?, Map<String?, RemoteCoinPriceRawData?>>?,
    @Json(name = "DISPLAY") val displayData: Map<String?, Map<String?, ServerCoinPriceDisplayData?>>?
)

@JsonClass(generateAdapter = true)
data class RemoteCoinPriceRawData(
    @Json(name = "TYPE") val type: String? = null,
    @Json(name = "MARKET") val market: String? = null,
    @Json(name = "FROMSYMBOL") val fromSymbol: String? = null,
    @Json(name = "TOSYMBOL") val toSymbol: String? = null,
    @Json(name = "FLAGS") val flags: String? = null,
    @Json(name = "PRICE") val price: Double? = null,
    @Json(name = "LASTUPDATE") val lastUpdate: Long? = null,
    @Json(name = "LASTVOLUME") val lastVolume: Double? = null,
    @Json(name = "LASTVOLUMETO") val lastVolumeTo: Double? = null,
    @Json(name = "LASTTRADEID") val lastTradeId: String? = null,
    @Json(name = "VOLUMEDAY") val volumeDay: Double? = null,
    @Json(name = "VOLUMEDAYTO") val volumeDayTo: Double? = null,
    @Json(name = "VOLUME24HOUR") val volume24Hour: Double? = null,
    @Json(name = "VOLUME24HOURTO") val volume24HourTo: Double? = null,
    @Json(name = "OPENDAY") val openDay: Double? = null,
    @Json(name = "HIGHDAY") val highDay: Double? = null,
    @Json(name = "LOWDAY") val lowDay: Double? = null,
    @Json(name = "OPEN24HOUR") val open24Hour: Double? = null,
    @Json(name = "HIGH24HOUR") val high24Hour: Double? = null,
    @Json(name = "LOW24HOUR") val low24Hour: Double? = null,
    @Json(name = "LASTMARKET") val lastMarket: String? = null,
    @Json(name = "CHANGE24HOUR") val change24Hour: Double? = null,
    @Json(name = "CHANGEPCT24HOUR") val changePct24Hour: Double? = null,
    @Json(name = "CHANGEDAY") val changeDay: Double? = null,
    @Json(name = "CHANGEPCTDAY") val changePctDay: Double? = null,
    @Json(name = "SUPPLY") val supply: Double? = null,
    @Json(name = "MKTCAP") val mktCap: Double? = null,
    @Json(name = "TOTALVOLUME24H") val totalVolume24Hour: Double? = null,
    @Json(name = "TOTALVOLUME24HTO") val totalVolume24HourTo: Double? = null
)

@JsonClass(generateAdapter = true)
class ServerCoinPriceDisplayData(
    @Json(name = "FROMSYMBOL") val fromSymbol: String?,
    @Json(name = "TOSYMBOL") val toSymbol: String?,
    @Json(name = "MARKET") val market: String?,
    @Json(name = "PRICE") val price: String?,
    @Json(name = "LASTUPDATE") val lastUpdate: String?,
    @Json(name = "LASTVOLUME") val lastVolume: String?,
    @Json(name = "LASTVOLUMETO") val lastVolumeTo: String?,
    @Json(name = "LASTTRADEID") val lastTradeId: String?,
    @Json(name = "VOLUMEDAY") val volumeDay: String?,
    @Json(name = "VOLUMEDAYTO") val volumeDayTo: String?,
    @Json(name = "VOLUME24HOUR") val volume24Hour: String?,
    @Json(name = "VOLUME24HOURTO") val volume24HourTo: String?,
    @Json(name = "OPENDAY") val openDay: String?,
    @Json(name = "HIGHDAY") val highDay: String?,
    @Json(name = "LOWDAY") val lowDay: String?,
    @Json(name = "OPEN24HOUR") val open24Hour: String?,
    @Json(name = "HIGH24HOUR") val high24Hour: String?,
    @Json(name = "LOW24HOUR") val low24Hour: String?,
    @Json(name = "LASTMARKET") val lastMarket: String?,
    @Json(name = "CHANGE24HOUR") val change24Hour: String?,
    @Json(name = "CHANGEPCT24HOUR") val changePct24Hour: String?,
    @Json(name = "CHANGEDAY") val changeDay: String?,
    @Json(name = "CHANGEPCTDAY") val changePctDay: String?,
    @Json(name = "SUPPLY") val supply: String?,
    @Json(name = "MKTCAP") val mktCap: String?,
    @Json(name = "TOTALVOLUME24H") val totalVolume24Hour: String?,
    @Json(name = "TOTALVOLUME24HTO") val totalVolume24HourTo: String?
)
