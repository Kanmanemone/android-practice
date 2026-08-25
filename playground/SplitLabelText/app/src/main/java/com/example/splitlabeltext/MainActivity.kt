package com.example.splitlabeltext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavBackStack
import com.example.splitlabeltext.entry.api.MyNavKey
import com.example.splitlabeltext.ui.theme.SplitLabelTextTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navBackStack: NavBackStack<MyNavKey>

    @Inject
    lateinit var entries: Set<@JvmSuppressWildcards MyNavKey>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitLabelTextTheme {
                SplitLabelTextApp(navBackStack, entries)
            }
        }
    }
}
