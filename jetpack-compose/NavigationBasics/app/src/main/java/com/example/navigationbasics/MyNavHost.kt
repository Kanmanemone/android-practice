package com.example.navigationbasics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "firstScreen"
    ) {
        // (1) FirstScreen
        composable(route = "firstScreen") {
            FirstScreen(navigateToSecondScreen = { navController.navigate("secondScreen") })
        }

        // (2) SecondScreen
        composable(route = "secondScreen") {
            SecondScreen(navigateToFirstScreen = { navController.navigate("firstScreen") })
        }
    }
}
