package com.charliechristensen.cryptotracker.data.websocket

/**
 * Maps response from web socket
 */
class SocketResponseMapper {
    companion object {
        fun mapResponse(response: Array<Any>): SocketResponse? {
            try {
                val responseString = response[0]
                if (responseString is String) {
                    val splitResponse = responseString.split("~")
                    if (splitResponse.size >= 5) {
                        val subscriptionId = splitResponse[0]
                        val exchangeName = splitResponse[1]
                        val fromCurrency = splitResponse[2]
                        val toCurrency = splitResponse[3]
                        val priceDirection = splitResponse[4]
                        val priceString = splitResponse[5]
                        val subscriptionIdInt = subscriptionId.toInt()
                        val priceDirectionInt = priceDirection.toInt()
                        val priceDouble = priceString.toDouble()
                        return SocketResponse(
                                subscriptionIdInt,
                                exchangeName,
                                fromCurrency,
                                toCurrency,
                                priceDirectionInt,
                                priceDouble)
                    }
                }
            } catch (e: Exception) {
                return null
            }
            return null
        }

    }
}

data class SocketResponse(val subscriptionId: Int,
                          val exchangeName: String,
                          val fromCurrency: String,
                          val toCurrency: String,
                          val priceDirection: Int,
                          val price: Double)