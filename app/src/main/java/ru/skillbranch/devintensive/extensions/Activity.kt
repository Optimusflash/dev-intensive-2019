package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard(){
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = this.currentFocus

    inputMethodManager.hideSoftInputFromWindow(view?.windowToken,0)
}

fun Activity.isKeyboardOpen(): Boolean {
    val rect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)
    return window.decorView.height - (rect.bottom - rect.top) > window.decorView.height / 4
}

fun Activity.isKeyboardClosed(): Boolean {
    return !isKeyboardOpen()
}