package com.example.argumentsofnavbackstackentry

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
        composable(
            route = "firstScreen"
        ) {
            FirstScreen(navigateToSecondScreen = { navController.navigate("secondScreen/HelloWorld/123") })
        }

        // (2) SecondScreen
        composable(
            route = "secondScreen/{sampleStringDataName}/{sampleIntDataName}",
            arguments = listOf(
                navArgument("sampleStringDataName") {
                    type = NavType.StringType
                },
                navArgument("sampleIntDataName") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            Log.i("interfacer_han", "${backStackEntry.arguments?.getString("sampleStringDataName")}")
            Log.i("interfacer_han", "${backStackEntry.arguments?.getInt("sampleIntDataName")}")

            SecondScreen(navigateToFirstScreen = { navController.navigate("firstScreen") })
        }
    }
}
