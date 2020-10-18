package com.charliechristensen.cryptotracker.common.extensions

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.charliechristensen.cryptotracker.MainApplication
import com.charliechristensen.cryptotracker.common.ColorUtils
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> FragmentActivity.viewModel(
    crossinline factory: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            factory() as T
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.viewModel(
    crossinline factory: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            factory() as T
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.savedStateViewModel(
    defaultArgs: Bundle? = null,
    crossinline factory: (SavedStateHandle) -> T
) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this, defaultArgs) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T = factory(handle) as T
    }
}

fun Context.getColorFromResource(colorAttribute: Int): Int {
    val typedValue = TypedValue()
    val theme = theme
    theme.resolveAttribute(colorAttribute, typedValue, true)
    return typedValue.data
}

//val Activity.injector
//    get() = (application as MainApplication).appComponent

//val Fragment.injector
//    get() = (requireActivity().application as MainApplication).appComponent

fun Fragment.showToast(@StringRes resId: Int) =
    Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()

fun Context.getColorAttribute(color: ValueChangeColor, success: (Int) -> Unit) {
    val typedValue = TypedValue()
    if (theme.resolveAttribute(ColorUtils.getColorInt(color), typedValue, true)) {
        success(typedValue.data)
    }
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
    val currentFocus = currentFocus
    if (currentFocus != null && imm != null) {
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}
