package com.finance.manager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.finance.manager.ui.screens.*
import com.finance.manager.ui.screens.auth.LoginScreen
import com.finance.manager.ui.screens.auth.RegisterScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(route = "login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {

                    navController.navigate(Screen.Home.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }


        composable(route = "register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {

                    navController.navigate(Screen.Home.route) {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }


        composable(route = Screen.Home.route) {
            HomeScreen()
        }


        composable(route = Screen.Transactions.route) {
            TransactionsScreen()
        }


        composable(route = Screen.Statistics.route) {
            StatisticsScreen()
        }


        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onLogout = {

                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        composable(route = Screen.Accounts.route) {
            AccountsScreen()
        }


        composable(route = Screen.Budgets.route) {
            BudgetsScreen()
        }


    }
}
