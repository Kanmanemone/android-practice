package com.example.mvvmbasics

import android.widget.EditText
import androidx.databinding.InverseMethod

object MyConverter {
    @InverseMethod("stringToInt")
    @JvmStatic
    fun intToString(
        view: EditText, value: Int
    ): String {
        return value.toString()
    }

    @JvmStatic
    fun stringToInt(
        view: EditText, value: String
    ): Int {
        return value.toIntOrNull() ?: 0
    }
}