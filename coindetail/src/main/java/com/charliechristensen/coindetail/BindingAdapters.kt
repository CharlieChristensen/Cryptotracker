package com.charliechristensen.coindetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.coindetail.ui.StyledLineGraphView
import com.charliechristensen.cryptotracker.common.ColorUtils
import com.charliechristensen.cryptotracker.common.extensions.getColorFromResource
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString

@BindingAdapter("colorValueString")
fun setColorValueString(textView: TextView, colorValueString: ColorValueString?) {
    if(colorValueString == null) return
    textView.text = colorValueString.value
    textView.setTextColor(
        textView.context.getColorFromResource(ColorUtils.getColorInt(colorValueString.color))
    )
}

@BindingAdapter("graphState")
fun setGraphState(styledLineGraphView: StyledLineGraphView, graphState: CoinDetailGraphState) {
    val context = styledLineGraphView.context
    when (graphState) {
        is CoinDetailGraphState.Success -> {
            val color = context.getColorFromResource(ColorUtils.getColorInt(graphState.color))
            val title =
                context.getString(com.charliechristensen.cryptotracker.cryptotracker.R.string.history)
            styledLineGraphView.setDataSet(graphState.coinHistoryList, title, color)
        }
        CoinDetailGraphState.Loading -> {
            styledLineGraphView.showLoading()
            styledLineGraphView.clear()
        }
        CoinDetailGraphState.NoData -> {
            styledLineGraphView.showNoData()
        }
        CoinDetailGraphState.Error -> {
            styledLineGraphView.showError()
        }
    }
}
