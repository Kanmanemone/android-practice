package com.example.twowaybindingusingconverter

import android.widget.EditText
import androidx.databinding.InverseMethod

object MyConverter {
    @InverseMethod("stringToInt")
    @JvmStatic
    fun intToString(
        view: EditText, value: Int
    ): String {
        // 사용자가 음수를 입력할 때, EditText.text가 "회" or "-회"이 되는 순간 "0회"로 갱신되지 않게 하기 위한 if문
        if (value == 0) {
            return view.text.toString()
        }

        return value.toString() + "회"
    }

    @JvmStatic
    fun stringToInt(
        view: EditText, value: String
    ): Int {
        println("뀨 [stringToInt] EditText.text: ${view.text}, value: ${value}")

        return value.replace("회", "").toIntOrNull() ?: 0
    }
}