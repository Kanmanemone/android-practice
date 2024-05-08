package com.example.automigrationspec

import android.widget.EditText
import androidx.databinding.InverseMethod

object MyConverter {
    @InverseMethod("stringToInt")
    @JvmStatic
    fun intToString(
        view: EditText, value: Int
    ): String {
        // 여기서 쓰인 Int형의 -1은 String형에서의 ""의 역할을 수행한다.
        if (value == -1) {
            return ""
        }

        return value.toString()
    }

    @JvmStatic
    fun stringToInt(
        view: EditText, value: String
    ): Int {
        // DB로 보낼 값인 temp가 반드시 -1이상의 정수가 되게끔 만듦.
        var temp = value.toIntOrNull() ?: -1
        temp = if (temp < -1) -1 else temp

        return temp
    }
}