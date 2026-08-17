package com.mjapa21.smartwallet.di

import com.mjapa21.smartwallet.presentation.SplashViewModel
import com.mjapa21.smartwallet.presentation.pages.home.HomeViewModel
import com.mjapa21.smartwallet.presentation.pages.login.LoginViewModel
import com.mjapa21.smartwallet.presentation.pages.transactions.TransactionHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::LoginViewModel)

    viewModelOf(::SplashViewModel)

    viewModelOf(::HomeViewModel)

    viewModelOf(::TransactionHistoryViewModel)

}