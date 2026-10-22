package com.example.swemobulletedlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavBackStack
import com.example.swemobulletedlist.entry.api.EntryKey
import com.example.swemobulletedlist.ui.SwemoBulletedListApp
import com.example.swemobulletedlist.ui.theme.SwemoBulletedListTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navBackStack: NavBackStack<EntryKey>

    @Inject
    lateinit var entryKeys: Set<@JvmSuppressWildcards EntryKey>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwemoBulletedListTheme {
                SwemoBulletedListApp(navBackStack, entryKeys)
            }
        }
    }
}
