package com.charliechristensen.coindetail.ui

import android.content.Context
import androidx.annotation.ColorInt
import android.util.AttributeSet
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * LineChart with styling applied to remove some boilerplate
 */
class StyledLineGraphView @JvmOverloads constructor(context: Context,
                                                    attrs: AttributeSet? = null,
                                                    defStyleAttr: Int = 0) : LineChart(context, attrs, defStyleAttr) {
    init {
        description.isEnabled = false
        axisLeft.setDrawLabels(false)
        axisRight.setDrawLabels(false)
        xAxis.setDrawLabels(false)
        legend.isEnabled = false
        axisLeft.setDrawLabels(false)
        axisLeft.setDrawGridLines(false)
        axisLeft.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        axisRight.setDrawGridLines(false)
        axisRight.setDrawAxisLine(false)
        setDrawBorders(false)
        setBorderColor(R.color.grey_500)
        setNoDataText("Loading...")
        setScaleEnabled(false)
        setViewPortOffsets(0f,8f,0f,0f)
        val priceDateMarkerView = PriceDateMarkerView(
            context,
            DecimalFormat("$###,###,###.##").apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 4
            },
            SimpleDateFormat("MM/d/yyyy, hh:mm a", Locale.getDefault())
        )//TODO PASS FORMATTER FROM CONTROLLER
        priceDateMarkerView.chartView = this
        marker = priceDateMarkerView
    }

    fun setDataSet(entries: List<Entry>, label: String, @ColorInt color: Int){
        val dataSet = LineDataSet(entries, label)
        dataSet.color = color
        dataSet.fillColor = color
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)
        dataSet.setDrawFilled(true)
        val lineData = LineData(dataSet)
        data = lineData
        animateY(500, Easing.EasingOption.EaseOutQuad)
    }

    fun showLoading(){
        setNoDataText("Loading...")
        invalidate()
    }

    fun showNoData(){
        setNoDataText("Data unavailable :(")
        invalidate()
    }

    fun showError(){
        setNoDataText("Error loading graph :(")
        invalidate()
    }

}