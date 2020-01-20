package com.charliechristensen.portfolio

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.charliechristensen.cryptotracker.common.ColorUtils
import com.charliechristensen.cryptotracker.common.GlideApp
import com.charliechristensen.cryptotracker.common.extensions.getColorAttribute
import com.charliechristensen.cryptotracker.common.extensions.getColorFromResource
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor

@BindingAdapter("imageUri")
fun loadImage(view: ImageView, imageUri: String) {
    GlideApp.with(view)
        .load(imageUri)
        .into(view)
}

@BindingAdapter("colorAttribute")
fun setColorAttribute(textView: TextView, valueChangeColor: ValueChangeColor) {
    textView.context.getColorAttribute(valueChangeColor) { color ->
        textView.setTextColor(color)
    }
}

@BindingAdapter("colorValueString")
fun setColorValueString(textView: TextView, colorValueString: ColorValueString?) {
    if(colorValueString == null) return
    textView.text = colorValueString.value
    textView.setTextColor(
        textView.context.getColorFromResource(ColorUtils.getColorInt(colorValueString.color))
    )
}
