package com.example.contentwithswipeablebottomsheet

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.contentwithswipeablebottomsheet.ui.SampleScreen
import com.example.contentwithswipeablebottomsheet.ui.theme.ContentWithSwipeableBottomSheetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setContent {
            ContentWithSwipeableBottomSheetTheme {
                SampleScreen()
            }
        }
    }
}