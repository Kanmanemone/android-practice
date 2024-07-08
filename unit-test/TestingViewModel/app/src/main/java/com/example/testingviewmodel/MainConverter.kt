package com.example.testingviewmodel

import android.widget.EditText
import androidx.databinding.InverseMethod

object MainConverter {
    @InverseMethod("stringToInt")
    @JvmStatic
    fun intToString(
        view: EditText, value: Int
    ): String {
        // 사용자가 음수를 입력할 때, EditText.text가 "" or "-"이 되는 순간 "0"으로 갱신되지 않게 하기 위한 if문
        if (value == 0) {
            return view.text.toString()
        }

        return value.toString()
    }

    @JvmStatic
    fun stringToInt(
        view: EditText, value: String
    ): Int {
        return value.toIntOrNull() ?: 0
    }
}