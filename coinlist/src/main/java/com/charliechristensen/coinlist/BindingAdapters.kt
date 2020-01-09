package com.charliechristensen.coinlist

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import com.charliechristensen.cryptotracker.common.GlideApp

@BindingAdapter("app:imageUri")
fun loadImage(view: ImageView, imageUri: String) {
    GlideApp.with(view)
        .load(imageUri)
        .apply(RequestOptions.circleCropTransform())
        .into(view)
}
