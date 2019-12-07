package ru.skillbranch.devintensive.extensions

import android.content.Context
import android.util.DisplayMetrics


fun Context.dpToPx(dp: Int): Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}

fun Context.pxToDp(px: Float): Float {
    return px / this.resources.displayMetrics.density
}