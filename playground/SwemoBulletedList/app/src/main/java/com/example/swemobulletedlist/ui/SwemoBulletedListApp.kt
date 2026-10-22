package com.example.swemobulletedlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.swemobulletedlist.entry.api.EntryKey
import com.example.swemobulletedlist.core.ui.ScreenInfoButton
import com.example.swemobulletedlist.core.ui.ScreenInfoDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwemoBulletedListApp(
    navBackStack: NavBackStack<EntryKey>,
    entryKeys: Set<EntryKey>
) {
    var screenInfoKey by remember { mutableStateOf<EntryKey?>(null) }
    screenInfoKey?.let { key ->
        ScreenInfoDialog(
            key = key,
            onDismiss = {
                screenInfoKey = null
            }
        )
    }

    val currentNavKey = navBackStack.lastOrNull()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(currentNavKey?.title.orEmpty())
                },
                actions = {
                    if (!currentNavKey?.description.isNullOrEmpty()) {
                        ScreenInfoButton(onClick = { screenInfoKey = currentNavKey })
                    }
                },
                navigationIcon = {
                    if (navBackStack.size > 1) {
                        IconButton(
                            onClick = { navBackStack.removeLastOrNull() },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = navBackStack,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            onBack = {
                navBackStack.removeLastOrNull()
            },
            entryProvider = entryProvider {
                entryKeys.forEach { key ->
                    addEntryProvider(key::class) {
                        Surface( // entry들 UI의 배경 색깔 기본값이 투명이라 Surface로 한번 감싸줌
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Box( // 소프트 키보드 영역 생성에 대한 반응 담당
                                modifier = Modifier
                                    .fillMaxSize()
                                    .imePadding()
                            ) {
                                it.screen()
                            }
                        }
                    }
                }
            },
        )
    }
}
