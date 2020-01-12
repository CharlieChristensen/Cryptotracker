package com.charliechristensen.coindetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.coindetail.ui.StyledLineGraphView
import com.charliechristensen.cryptotracker.common.ColorUtils
import com.charliechristensen.cryptotracker.common.extensions.getColorFromResource
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.google.android.material.tabs.TabLayout

@BindingAdapter("colorValueString")
fun setColorValueString(textView: TextView, colorValueString: ColorValueString?) {
    if (colorValueString == null) return
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

interface TabSelectedListener {
    fun invoke(position: Int)
}

@BindingAdapter("android:selectedTab")
fun selectedTab(tabLayout: TabLayout, index: Int) {
    if (tabLayout.selectedTabPosition == index) {
        return
    }
    tabLayout.getTabAt(index)?.select()
}

@BindingAdapter("android:onTabSelected")
fun setListener(tabLayout: TabLayout, listener: TabSelectedListener?) {
    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab) {

        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            listener?.invoke(tab.position)
        }

    })
}
