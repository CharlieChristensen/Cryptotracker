@file:Suppress("unused")

package com.charliechristensen.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteHistoryResponse(
    @Json(name = "Response") val response: String,
    @Json(name = "Message") val message: String?,
    @Json(name = "Type") val type: Int,
    @Json(name = "Aggregated") val aggregated: Boolean,
    @Json(name = "Data") val data: List<RemoteHistoryElement>,
    @Json(name = "TimeTo") val timeTo: Long,
    @Json(name = "TimeFrom") val timeFrom: Long,
    @Json(name = "FirstValueInArray") val firstValueInArray: Boolean,
    @Json(name = "ConversionType") val conversionType: RemoteHistoryConversionType
)

@JsonClass(generateAdapter = true)
data class RemoteHistoryConversionType(
    @Json(name = "type") val type: String,
    @Json(name = "conversionSymbol") val conversionSymbol: String
)
