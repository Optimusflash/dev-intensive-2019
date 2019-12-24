package ru.skillbranch.devintensive.extensions

import android.util.TypedValue
import android.view.View

fun View.getColorByThemeAttr(resId: Int): Int{
    val typedValue = TypedValue()
    this.context.theme.resolveAttribute(resId, typedValue, true)
    return typedValue.data
}