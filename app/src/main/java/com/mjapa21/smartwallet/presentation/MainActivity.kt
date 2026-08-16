package com.mjapa21.smartwallet.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjapa21.smartwallet.presentation.navigation.NavigationRoot
import com.mjapa21.smartwallet.ui.theme.SmartWalletTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.startDestination.value == null
        }

        enableEdgeToEdge()
        setContent {
            val startDestination by splashViewModel.startDestination.collectAsStateWithLifecycle()

            startDestination?.let {
                SmartWalletTheme {
                    NavigationRoot(it)
                }
            }
        }
    }
}