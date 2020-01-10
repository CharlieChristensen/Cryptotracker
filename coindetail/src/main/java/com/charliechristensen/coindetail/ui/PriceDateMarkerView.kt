package com.charliechristensen.coindetail.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import com.charliechristensen.coindetail.R
import com.charliechristensen.coindetail.databinding.ViewPriceDateMarkerBinding
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
class PriceDateMarkerView @JvmOverloads constructor(
    context: Context,
    private val priceFormatter: DecimalFormat = DecimalFormat(),
    private val dateFormatter: SimpleDateFormat = SimpleDateFormat()
) : MarkerView(context, R.layout.view_price_date_marker) {

    private val binding = ViewPriceDateMarkerBinding.bind(this.getChildAt(0))

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        if (entry == null) return
        binding.priceTextView.text = priceFormatter.format(entry.y)
        binding.dateTextView.text = dateFormatter.format(entry.x.toLong() * 1000L)
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), 0f)
    }

    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        val offset = getOffsetForDrawingAtPoint(posX, posY)
        val saveId = canvas.save()
        canvas.translate(posX + offset.x, 0f)
        draw(canvas)
        canvas.restoreToCount(saveId)
    }
}
