package com.example.splitlabeltext

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.example.splitlabeltext.entry.api.MyNavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitLabelTextApp(
    navBackStack: NavBackStack<MyNavKey>,
    entries: Set<MyNavKey>
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(navBackStack.lastOrNull()?.title.orEmpty())
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
            /* consumeWindowInsets()
             * innerPadding을 "소비 처리"해서,
             * 자식 컴포저블이 호출하는 imePadding() 등의 반환 값에 innerPadding 값이 포함되지 않게 한다.
             * 이 코드가 없을 때의 예:
             * 시스템 네비게이션 바의 높이가 10이고 ime의 높이가 50일 때,
             * 프로그래머는 (ime 활성화 때) ui가 50만큼만 위로 밀리길 기대하지만,
             * 실제론 10 + 50 = 60이 밀려버린다!
             */
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            onBack = { navBackStack.removeLastOrNull() },
            transitionSpec = forwardTransitionSpec,
            popTransitionSpec = backTransitionSpec,
            predictivePopTransitionSpec = { backTransitionSpec() },
            entryProvider = entryProvider {
                entries.forEach { key ->
                    addEntryProvider(key::class) {
                        // 전환 애니메이션 중 뒤 화면이 비쳐 보이지 않기 위해 깔아놓는 불투명 배경 (Surface)
                        Surface(modifier = Modifier.fillMaxSize()) {
                            // 키보드가 올라온 만큼만 내용을 밀어올리는 역할 담당 컨테이너 (Box)
                            Box(
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

private val forwardTransitionSpec: AnimatedContentTransitionScope<Scene<MyNavKey>>.() -> ContentTransform =
    {
        ContentTransform(
            slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }),
            slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth / 4 }),
        )
    }

private val backTransitionSpec: AnimatedContentTransitionScope<Scene<MyNavKey>>.() -> ContentTransform =
    {
        ContentTransform(
            slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth / 4 }),
            slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }),
        )
    }