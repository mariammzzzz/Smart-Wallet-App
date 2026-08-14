package com.mjapa21.smartwallet.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.mjapa21.smartwallet.presentation.pages.home.HomeScreen
import com.mjapa21.smartwallet.presentation.pages.login.LoginScreen

@Composable
fun NavigationRoot() {

    val backStack = rememberNavBackStack(Destinations.Login) //todo check this later
    NavDisplay(backStack = backStack) { navKey ->
        when (navKey) {
            is Destinations.Home -> NavEntry(navKey) {
                HomeScreen()
            }

            is Destinations.Login -> NavEntry(navKey) {
                LoginScreen(onRegistrationComplete = {})
            }

            else -> throw IllegalStateException("Unknown destination: $navKey")
        }
    }

}