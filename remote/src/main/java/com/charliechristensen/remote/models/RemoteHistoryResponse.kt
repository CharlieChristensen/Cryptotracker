@file:Suppress("unused")

package com.charliechristensen.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteHistoryResponse(
    @SerialName("Response") val response: String? = null,
    @SerialName("Message") val message: String? = null,
    @SerialName("Type") val type: Int? = null,
    @SerialName("Aggregated") val aggregated: Boolean? = null,
    @SerialName("Data") val data: List<RemoteHistoryElement?>? = null,
    @SerialName("TimeTo") val timeTo: Long? = null,
    @SerialName("TimeFrom") val timeFrom: Long? = null,
    @SerialName("FirstValueInArray") val firstValueInArray: Boolean? = null,
    @SerialName("ConversionType") val conversionType: RemoteHistoryConversionType? = null
)

@Serializable
data class RemoteHistoryConversionType(
    @SerialName("type") val type: String? = null,
    @SerialName("conversionSymbol") val conversionSymbol: String? = null
)
