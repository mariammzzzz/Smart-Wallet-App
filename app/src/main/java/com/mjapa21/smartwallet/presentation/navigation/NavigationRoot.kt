package com.mjapa21.smartwallet.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.mjapa21.smartwallet.presentation.pages.HomeScreen
import com.mjapa21.smartwallet.presentation.pages.LoginScreen

@Composable
fun NavigationRoot() {

    val backStack = rememberNavBackStack(Destinations.Login) //todo check this later
    NavDisplay(backStack = backStack) { navKey ->
        when (navKey) {
            is Destinations.Home -> NavEntry(navKey) {
                HomeScreen()
            }

            is Destinations.Login -> NavEntry(navKey) {
                LoginScreen(onSubmit = { _, _, _, _, _ -> //todo handle login submission

                })
            }

            else -> throw IllegalStateException("Unknown destination: $navKey")
        }
    }

}