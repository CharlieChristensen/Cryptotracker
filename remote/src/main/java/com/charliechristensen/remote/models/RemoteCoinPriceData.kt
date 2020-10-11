@file:Suppress("unused")

package com.charliechristensen.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTopListCoinData(
    @SerialName("RAW") val rawData: Map<String?, Map<String?, RemoteCoinPriceRawData?>>? = null,
    @SerialName("DISPLAY") val displayData: Map<String?, Map<String?, ServerCoinPriceDisplayData?>>? = null
)

@SuppressWarnings("SpellCheckingInspection")
@Serializable
data class RemoteCoinPriceData(
    @SerialName("RAW") val rawData: Map<String?, Map<String?, RemoteCoinPriceRawData?>>? = null,
    @SerialName("DISPLAY") val displayData: Map<String?, Map<String?, ServerCoinPriceDisplayData?>>? = null
)

@Serializable
data class RemoteCoinPriceRawData(
    @SerialName("TYPE") val type: String? = null,
    @SerialName("MARKET") val market: String? = null,
    @SerialName("FROMSYMBOL") val fromSymbol: String? = null,
    @SerialName("TOSYMBOL") val toSymbol: String? = null,
    @SerialName("FLAGS") val flags: String? = null,
    @SerialName("PRICE") val price: Double? = null,
    @SerialName("LASTUPDATE") val lastUpdate: Long? = null,
    @SerialName("LASTVOLUME") val lastVolume: Double? = null,
    @SerialName("LASTVOLUMETO") val lastVolumeTo: Double? = null,
    @SerialName("LASTTRADEID") val lastTradeId: String? = null,
    @SerialName("VOLUMEDAY") val volumeDay: Double? = null,
    @SerialName("VOLUMEDAYTO") val volumeDayTo: Double? = null,
    @SerialName("VOLUME24HOUR") val volume24Hour: Double? = null,
    @SerialName("VOLUME24HOURTO") val volume24HourTo: Double? = null,
    @SerialName("OPENDAY") val openDay: Double? = null,
    @SerialName("HIGHDAY") val highDay: Double? = null,
    @SerialName("LOWDAY") val lowDay: Double? = null,
    @SerialName("OPEN24HOUR") val open24Hour: Double? = null,
    @SerialName("HIGH24HOUR") val high24Hour: Double? = null,
    @SerialName("LOW24HOUR") val low24Hour: Double? = null,
    @SerialName("LASTMARKET") val lastMarket: String? = null,
    @SerialName("CHANGE24HOUR") val change24Hour: Double? = null,
    @SerialName("CHANGEPCT24HOUR") val changePct24Hour: Double? = null,
    @SerialName("CHANGEDAY") val changeDay: Double? = null,
    @SerialName("CHANGEPCTDAY") val changePctDay: Double? = null,
    @SerialName("SUPPLY") val supply: Double? = null,
    @SerialName("MKTCAP") val mktCap: Double? = null,
    @SerialName("TOTALVOLUME24H") val totalVolume24Hour: Double? = null,
    @SerialName("TOTALVOLUME24HTO") val totalVolume24HourTo: Double? = null
)

@Serializable
class ServerCoinPriceDisplayData(
    @SerialName("FROMSYMBOL") val fromSymbol: String? = null,
    @SerialName("TOSYMBOL") val toSymbol: String? = null,
    @SerialName("MARKET") val market: String? = null,
    @SerialName("PRICE") val price: String? = null,
    @SerialName("LASTUPDATE") val lastUpdate: String? = null,
    @SerialName("LASTVOLUME") val lastVolume: String? = null,
    @SerialName("LASTVOLUMETO") val lastVolumeTo: String? = null,
    @SerialName("LASTTRADEID") val lastTradeId: String? = null,
    @SerialName("VOLUMEDAY") val volumeDay: String? = null,
    @SerialName("VOLUMEDAYTO") val volumeDayTo: String? = null,
    @SerialName("VOLUME24HOUR") val volume24Hour: String? = null,
    @SerialName("VOLUME24HOURTO") val volume24HourTo: String? = null,
    @SerialName("OPENDAY") val openDay: String? = null,
    @SerialName("HIGHDAY") val highDay: String? = null,
    @SerialName("LOWDAY") val lowDay: String? = null,
    @SerialName("OPEN24HOUR") val open24Hour: String? = null,
    @SerialName("HIGH24HOUR") val high24Hour: String? = null,
    @SerialName("LOW24HOUR") val low24Hour: String? = null,
    @SerialName("LASTMARKET") val lastMarket: String? = null,
    @SerialName("CHANGE24HOUR") val change24Hour: String? = null,
    @SerialName("CHANGEPCT24HOUR") val changePct24Hour: String? = null,
    @SerialName("CHANGEDAY") val changeDay: String? = null,
    @SerialName("CHANGEPCTDAY") val changePctDay: String? = null,
    @SerialName("SUPPLY") val supply: String? = null,
    @SerialName("MKTCAP") val mktCap: String? = null,
    @SerialName("TOTALVOLUME24H") val totalVolume24Hour: String? = null,
    @SerialName("TOTALVOLUME24HTO") val totalVolume24HourTo: String? = null
)
