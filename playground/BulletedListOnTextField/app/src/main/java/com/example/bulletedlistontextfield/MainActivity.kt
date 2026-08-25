package com.example.bulletedlistontextfield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavBackStack
import com.example.bulletedlistontextfield.entry.api.MyNavKey
import com.example.bulletedlistontextfield.ui.theme.BulletedListOnTextFieldTheme
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
            BulletedListOnTextFieldTheme {
                BulletedListOnTextFieldApp(navBackStack, entries)
            }
        }
    }
}
