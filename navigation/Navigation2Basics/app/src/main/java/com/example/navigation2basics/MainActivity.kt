package com.example.navigation2basics

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.navigation2basics.ui.theme.Navigation2BasicsTheme
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data class Article(val id: String)

@Serializable
data object Setting

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation2BasicsTheme {
                MyApp()
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStack.collectAsState() // 백스택이 NavHostController 자체에 박혀있는 모습
    val backStackSize = backStack.count {
        it.destination !is NavGraph // "!is NavGraph"가 없으면 'Root NavGraph' 등 내가 신경쓰지 않아도 되는 것들까지 다 size로서 잡혀버린다
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Back stack size: $backStackSize") },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<Home> {
                HomeRoute(
                    navigateToArticle = { articleId ->
                        navController.navigate(Article(articleId))
                    },
                    navigateToSetting = {
                        navController.navigate(Setting)
                    }
                )
            }

            composable<Article> { backStackEntry ->
                val article = backStackEntry.toRoute<Article>()

                ArticleRoute(
                    id = article.id,
                    navigateToHome = {
                        navController.navigate(Home)
                    },
                    navigateToSetting = {
                        navController.navigate(Setting)
                    },
                )
            }

            composable<Setting> {
                SettingRoute(
                    navigateToHome = {
                        navController.navigate(Home)
                    },
                    navigateToArticle = { articleId ->
                        navController.navigate(Article(articleId))
                    },
                )
            }
        }
    }
}

@Composable
fun HomeRoute(
    navigateToArticle: (String) -> Unit,
    navigateToSetting: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Home")

        Button(
            onClick = {
                navigateToArticle("7")
            }
        ) {
            Text("Go to 7th Article")
        }

        Button(
            onClick = {
                navigateToSetting()
            }
        ) {
            Text("Go to Setting")
        }
    }
}

@Composable
fun ArticleRoute(
    id: String,
    navigateToHome: () -> Unit,
    navigateToSetting: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("${id}th Article")

        Button(
            onClick = {
                navigateToHome()
            }
        ) {
            Text("Go to Home")
        }

        Button(
            onClick = {
                navigateToSetting()
            }
        ) {
            Text("Go to Setting")
        }
    }
}

@Composable
fun SettingRoute(
    navigateToHome: () -> Unit,
    navigateToArticle: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Setting")

        Button(
            onClick = {
                navigateToHome()
            }
        ) {
            Text("Go to Home")
        }

        Button(
            onClick = {
                navigateToArticle("8")
            }
        ) {
            Text("Go to 8th Article")
        }
    }
}