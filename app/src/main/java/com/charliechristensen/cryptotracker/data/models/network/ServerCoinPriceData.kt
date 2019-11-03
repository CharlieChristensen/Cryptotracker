@file:Suppress("unused")

package com.charliechristensen.cryptotracker.data.models.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@SuppressWarnings("SpellCheckingInspection")
@JsonClass(generateAdapter = true)
data class ServerCoinPriceData(
    @Json(name = "RAW") val rawData: Map<String, Map<String, ServerCoinPriceRawData>>?,
    @Json(name = "DISPLAY") val displayData: Map<String, Map<String, ServerCoinPriceDisplayData>>
)

@JsonClass(generateAdapter = true)
data class ServerCoinPriceRawData(
    @Json(name = "TYPE") val type: String,
    @Json(name = "MARKET") val market: String,
    @Json(name = "FROMSYMBOL") val fromSymbol: String,
    @Json(name = "TOSYMBOL") val toSymbol: String,
    @Json(name = "FLAGS") val flags: String,
    @Json(name = "PRICE") val price: Double,
    @Json(name = "LASTUPDATE") val lastUpdate: Long,
    @Json(name = "LASTVOLUME") val lastVolume: Double,
    @Json(name = "LASTVOLUMETO") val lastVolumeTo: Double,
    @Json(name = "LASTTRADEID") val lastTradeId: Double,
    @Json(name = "VOLUMEDAY") val volumeDay: Double,
    @Json(name = "VOLUMEDAYTO") val volumeDayTo: Double,
    @Json(name = "VOLUME24HOUR") val volume24Hour: Double,
    @Json(name = "VOLUME24HOURTO") val volume24HourTo: Double,
    @Json(name = "OPENDAY") val openDay: Double,
    @Json(name = "HIGHDAY") val highDay: Double,
    @Json(name = "LOWDAY") val lowDay: Double,
    @Json(name = "OPEN24HOUR") val open24Hour: Double,
    @Json(name = "HIGH24HOUR") val high24Hour: Double,
    @Json(name = "LOW24HOUR") val low24Hour: Double,
    @Json(name = "LASTMARKET") val lastMarket: String,
    @Json(name = "CHANGE24HOUR") val change24Hour: Double,
    @Json(name = "CHANGEPCT24HOUR") val changePct24Hour: Double,
    @Json(name = "CHANGEDAY") val changeDay: Double,
    @Json(name = "CHANGEPCTDAY") val changePctDay: Double,
    @Json(name = "SUPPLY") val supply: Double,
    @Json(name = "MKTCAP") val mktCap: Double,
    @Json(name = "TOTALVOLUME24H") val totalVolume24Hour: Double,
    @Json(name = "TOTALVOLUME24HTO") val totalVolume24HourTo: Double
)

@JsonClass(generateAdapter = true)
class ServerCoinPriceDisplayData(
    @Json(name = "FROMSYMBOL") val fromSymbol: String,
    @Json(name = "TOSYMBOL") val toSymbol: String,
    @Json(name = "MARKET") val market: String,
    @Json(name = "PRICE") val price: String,
    @Json(name = "LASTUPDATE") val lastUpdate: String,
    @Json(name = "LASTVOLUME") val lastVolume: String,
    @Json(name = "LASTVOLUMETO") val lastVolumeTo: String,
    @Json(name = "LASTTRADEID") val lastTradeId: String,
    @Json(name = "VOLUMEDAY") val volumeDay: String,
    @Json(name = "VOLUMEDAYTO") val volumeDayTo: String,
    @Json(name = "VOLUME24HOUR") val volume24Hour: String,
    @Json(name = "VOLUME24HOURTO") val volume24HourTo: String,
    @Json(name = "OPENDAY") val openDay: String,
    @Json(name = "HIGHDAY") val highDay: String,
    @Json(name = "LOWDAY") val lowDay: String,
    @Json(name = "OPEN24HOUR") val open24Hour: String,
    @Json(name = "HIGH24HOUR") val high24Hour: String,
    @Json(name = "LOW24HOUR") val low24Hour: String,
    @Json(name = "LASTMARKET") val lastMarket: String,
    @Json(name = "CHANGE24HOUR") val change24Hour: String,
    @Json(name = "CHANGEPCT24HOUR") val changePct24Hour: String,
    @Json(name = "CHANGEDAY") val changeDay: String,
    @Json(name = "CHANGEPCTDAY") val changePctDay: String,
    @Json(name = "SUPPLY") val supply: String,
    @Json(name = "MKTCAP") val mktCap: String,
    @Json(name = "TOTALVOLUME24H") val totalVolume24Hour: String,
    @Json(name = "TOTALVOLUME24HTO") val totalVolume24HourTo: String
)
