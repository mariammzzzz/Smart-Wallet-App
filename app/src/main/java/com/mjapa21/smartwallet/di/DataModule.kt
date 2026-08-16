package com.mjapa21.smartwallet.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mjapa21.smartwallet.data.local.UserPreferencesRepositoryImpl
import com.mjapa21.smartwallet.data.local.userDataStore
import com.mjapa21.smartwallet.domain.repository.UserPreferencesRepository
import com.mjapa21.smartwallet.domain.usecases.GetUserUseCase
import com.mjapa21.smartwallet.domain.usecases.SaveUserUseCase
import org.koin.dsl.module

val dataModule = module {

    // androidContext() passed to startKoin { } in SmartWalletApplication registers the
    // Application Context into the Koin graph; then we acquire it by get<Context>()
    single<DataStore<Preferences>> { get<Context>().userDataStore }

    //by get() we use the DataStore<Preferences> instance we just registered above
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }


    //USECASES
    factory<SaveUserUseCase> { SaveUserUseCase(get()) }

    factory<GetUserUseCase> { GetUserUseCase(get()) }
}