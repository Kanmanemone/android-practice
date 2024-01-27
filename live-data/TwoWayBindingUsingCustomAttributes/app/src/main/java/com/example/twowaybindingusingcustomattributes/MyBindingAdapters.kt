package com.example.twowaybindingusingcustomattributes

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter

object MyBindingAdapters {
    @BindingAdapter("android:text")
    @JvmStatic
    fun setCountText(editText: EditText, value: Int?) {
        // 사용자가 음수를 입력할 때, EditText.text가 "회" or "-회"이 되는 순간 "0회"로 갱신되지 않게 하기 위한 if문
        if ((editText.text.toString().replace("회", "").toIntOrNull() ?: 0) == (value ?: 0)) {
            return
        }

        editText.setText(value?.toString() + "회")
    }

    @InverseBindingAdapter(attribute = "android:text")
    @JvmStatic
    fun getCountText(editText: EditText): Int {
        return editText.text.toString().replace("회", "").toIntOrNull() ?: 0
    }
}

/*
위의 코드는 (https://developer.android.com/topic/libraries/data-binding/two-way?hl=ko)에 있는 코드를 참조한 코드.
그 코드는 다음과 같다.

    예를 들어 MyView라는 맞춤 뷰의 "time" 속성에 양방향 데이터 결합을 사용하는 경우,

    @BindingAdapter("time")
    @JvmStatic fun setTime(view: MyView, newValue: Time) {
        // Important to break potential infinite loops.
        if (view.time != newValue) {
            view.time = newValue
        }
    }

    @InverseBindingAdapter("time")
    @JvmStatic fun getTime(view: MyView) : Time {
        return view.getTime()
    }
*/