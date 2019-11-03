package com.charliechristensen.coindetail

import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor

data class CoinDetailData(
    val coinIsInPortfolio: Boolean = false,
    val walletUnitsOwned: Double = 0.0,
    val pricePerUnit: Double = 0.0,
    val pricePerUnit24HourLow: Double = 0.0,
    val pricePerUnit24HourHigh: Double = 0.0,
    val walletTotalValue: Double = 0.0,
    val walletPriceChange24Hour: ColorValueString = ColorValueString(
        "",
        ValueChangeColor.RED
    ),
    val toolbarImageData: ImageAndNamePair = ImageAndNamePair()
)

//data class CoinDetailDatas(
//    val coinIsInPortfolio: CoinDetailField<Boolean> = CoinDetailField(false),
//    val walletUnitsOwned: CoinDetailField<Double> = CoinDetailField(0.0),
//    val pricePerUnit: CoinDetailField<Double> = CoinDetailField(0.0),
//    val pricePerUnit24HourLow: CoinDetailField<Double> = 0.0,
//    val pricePerUnit24HourHigh: Double = 0.0,
//    val walletTotalValue: Double = 0.0,
//    val walletPriceChange24Hour: ColorValueString = ColorValueString(
//        "",
//        ValueChangeColor.RED
//    ),
//    val toolbarImageData: ImageAndNamePair = ImageAndNamePair()
//) {
//
//    fun compare(other: CoinDetailDatas) {
//
//    }
//
//}
//
//class CoinDetailField<T>(
//    val value: T,
//    val isDirty: Boolean = true
//)
