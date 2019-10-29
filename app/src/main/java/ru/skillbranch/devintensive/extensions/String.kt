package ru.skillbranch.devintensive.extensions

fun String.truncate(length: Int = 16): String {
    val newString = this.trim()
    return when {
        newString.length <= length -> newString
        else -> "${newString.take(length).trimEnd()}..."
    }
}

fun String.stripHtml(): String = this
    .replace(Regex("\\s{2,}"), " ")
    .replace(Regex("<.*?>|&#\\d+?|\\w+?;"), "")