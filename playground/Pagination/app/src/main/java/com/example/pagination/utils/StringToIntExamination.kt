package com.example.pagination.utils

fun canCastToNonNegativeInt(string: String): Boolean {
    val mayBeIntValue = string.toIntOrNull()
    return (mayBeIntValue != null) && (0 <= mayBeIntValue)
}

fun canCastToPositiveInt(string: String): Boolean {
    val mayBeIntValue = string.toIntOrNull()
    return (mayBeIntValue != null) && (0 < mayBeIntValue)
}