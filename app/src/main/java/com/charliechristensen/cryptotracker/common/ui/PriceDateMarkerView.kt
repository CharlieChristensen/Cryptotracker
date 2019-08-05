package com.charliechristensen.cryptotracker.common.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.view_price_date_marker.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * Created by Chuck on 1/8/2018.
 */
@SuppressLint("SimpleDateFormat")
class PriceDateMarkerView @JvmOverloads constructor(
    context: Context,
    private val priceFormatter: DecimalFormat = DecimalFormat(),
    private val dateFormatter: SimpleDateFormat = SimpleDateFormat()
) : MarkerView(context, R.layout.view_price_date_marker) {

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        if (entry == null) return
        priceTextView.text = priceFormatter.format(entry.y)
        dateTextView.text = dateFormatter.format(entry.x.toLong() * 1000L)
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), 0f)
    }

    override fun draw(canvas: Canvas, posX: Float, posY: Float) {

        val offset = getOffsetForDrawingAtPoint(posX, posY)

        val saveId = canvas.save()
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, 0f)
        draw(canvas)
        canvas.restoreToCount(saveId)
    }

}