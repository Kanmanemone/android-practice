package com.example.navigation3basics

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.navigation3basics.ui.theme.Navigation3BasicsTheme
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Serializable
data class Article(val id: String) : NavKey

@Serializable
data object Setting : NavKey

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Navigation3BasicsTheme {
                MyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val backStack = rememberNavBackStack(Home)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Back stack size: ${backStack.size}")
                },
            )
        },
    ) { innerPadding ->

        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            onBack = {
                backStack.removeLastOrNull()
            },
            entryProvider = entryProvider {
                entry<Home> {
                    HomeRoute(
                        navigateToArticle = { articleId ->
                            backStack.add(Article(articleId))
                        },
                        navigateToSetting = {
                            backStack.add(Setting)
                        },
                    )
                }

                entry<Article> { article ->
                    ArticleRoute(
                        id = article.id,
                        navigateToHome = {
                            backStack.add(Home)
                        },
                        navigateToSetting = {
                            backStack.add(Setting)
                        },
                    )
                }

                entry<Setting> {
                    SettingRoute(
                        navigateToHome = {
                            backStack.add(Home)
                        },
                        navigateToArticle = { articleId ->
                            backStack.add(Article(articleId))
                        },
                    )
                }
            },
        )
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