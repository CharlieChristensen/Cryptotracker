@file:Suppress("unused")

package com.charliechristensen.cryptotracker.data.models.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ServerHistoryResponse(@Json(name = "Response")val response: String,
                            @Json(name = "Message") val message: String?,
                            @Json(name = "Type")val type: Int,
                            @Json(name = "Aggregated")val aggregated: Boolean,
                            @Json(name = "Data")val data: List<ServerHistoryElement>,
                            @Json(name = "TimeTo")val timeTo: Long,
                            @Json(name = "TimeFrom")val timeFrom: Long,
                            @Json(name = "FirstValueInArray")val firstValueInArray: Boolean,
                            @Json(name = "ConversionType")val conversionType: ServerHistoryConversionType
)

@JsonClass(generateAdapter = true)
class ServerHistoryConversionType(val type: String,
                                  val conversionSymbol: String)