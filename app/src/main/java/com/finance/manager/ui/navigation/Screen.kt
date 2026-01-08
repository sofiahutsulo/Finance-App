package com.finance.manager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Screen(val route: String) {

    object Auth : Screen("auth")


    object Home : Screen("home")


    object Transactions : Screen("transactions")


    object Accounts : Screen("accounts")


    object Statistics : Screen("statistics")


    object Budgets : Screen("budgets")


    object Profile : Screen("profile")


    object AdminPanel : Screen("admin_panel")
}


sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        screen = Screen.Home,
        title = "Головна",
        icon = Icons.Filled.Home
    )

    object Transactions : BottomNavItem(
        screen = Screen.Transactions,
        title = "Транзакції",
        icon = Icons.Filled.List
    )

    object Accounts : BottomNavItem(
        screen = Screen.Accounts,
        title = "Рахунки",
        icon = Icons.Filled.AccountBalance
    )

    object Statistics : BottomNavItem(
        screen = Screen.Statistics,
        title = "Статистика",
        icon = Icons.Filled.BarChart
    )

    object Profile : BottomNavItem(
        screen = Screen.Profile,
        title = "Профіль",
        icon = Icons.Filled.Person
    )
}


val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Transactions,
    BottomNavItem.Accounts,
    BottomNavItem.Statistics,
    BottomNavItem.Profile
)
