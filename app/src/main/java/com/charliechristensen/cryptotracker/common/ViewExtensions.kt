package com.charliechristensen.cryptotracker.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.charliechristensen.cryptotracker.MainApplication
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor

inline fun <reified T : Fragment> fragment(block: Bundle.() -> Unit): T =
    T::class.java.newInstance().apply {
        arguments = Bundle().apply(block)
    }

inline fun <reified T : AppCompatActivity> intent(
    context: Context?,
    block: Intent.() -> Unit
): Intent =
    Intent(context, T::class.java).apply(block)

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

fun Fragment.pushFragment(
    fragment: Fragment,
    @IdRes containerViewId: Int = R.id.contentView,
    addToBackStack: Boolean = true
): Int = requireActivity().pushFragment(fragment, containerViewId, addToBackStack)

fun FragmentActivity.pushFragment(
    fragment: Fragment,
    @IdRes containerViewId: Int = R.id.contentView,
    addToBackStack: Boolean = true
): Int =
    supportFragmentManager.beginTransaction().apply {
        setCustomAnimations(
            R.anim.slide_enter_from_right,
            R.anim.slide_exit_to_left,
            R.anim.slide_enter_from_left,
            R.anim.slide_exit_to_right
        )
        replace(containerViewId, fragment)
        if (addToBackStack) {
            addToBackStack(null)
        }
    }.commit()


val Activity.injector
    get() = (application as MainApplication).appComponent

val Fragment.injector
    get() = (requireActivity().application as MainApplication).appComponent

fun Fragment.showToast(@StringRes resId: Int) =
    Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()

fun Fragment.showToast(message: String) =
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

//TODO Caching colors might run faster
fun Context.getColorAttribute(color: ValueChangeColor, success: (Int) -> Unit) {
    val typedValue = TypedValue()
    if (theme.resolveAttribute(ColorUtils.getColorInt(color), typedValue, true)) {
        success(typedValue.data)
    }
}