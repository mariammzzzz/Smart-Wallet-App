package com.mjapa21.smartwallet.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.mjapa21.smartwallet.data.local.datastore.UserPreferencesRepositoryImpl
import com.mjapa21.smartwallet.data.local.datastore.userDataStore
import com.mjapa21.smartwallet.data.local.db.SmartWalletDatabase
import com.mjapa21.smartwallet.data.local.db.repository.CardRepositoryImpl
import com.mjapa21.smartwallet.data.local.db.repository.TransactionsRepositoryImpl
import com.mjapa21.smartwallet.domain.repository.CardRepository
import com.mjapa21.smartwallet.domain.repository.TransactionsRepository
import com.mjapa21.smartwallet.domain.repository.UserPreferencesRepository
import com.mjapa21.smartwallet.domain.usecases.GetCardDetailsUseCase
import com.mjapa21.smartwallet.domain.usecases.GetUserUseCase
import com.mjapa21.smartwallet.domain.usecases.SaveUserWithCardUseCase
import org.koin.dsl.module

val dataModule = module {

    // androidContext() passed to startKoin { } in SmartWalletApplication registers the
    // Application Context into the Koin graph; then we acquire it by get<Context>()
    single<DataStore<Preferences>> { get<Context>().userDataStore }

    //by get() we use the DataStore<Preferences> instance we just registered above
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }


    //USECASES
    factory<GetUserUseCase> { GetUserUseCase(get()) }


    //DB
    single {
        Room.databaseBuilder(
            get<Context>(),
            SmartWalletDatabase::class.java,
            "smart-wallet-db"
        ).build()
    }

    //DAO-s (for the repositories)
    single { get<SmartWalletDatabase>().cardDao() }
    single { get<SmartWalletDatabase>().transactionDao() }

    //db REPOs
    single<TransactionsRepository> { TransactionsRepositoryImpl(get()) }
    single<CardRepository> { CardRepositoryImpl(get()) }


    //more usecases
    factory<SaveUserWithCardUseCase> {
        SaveUserWithCardUseCase(
            get(),
            get()
        )
    } //todo check does it matter where its declared? after repos or before?

    factory<GetCardDetailsUseCase> {
        GetCardDetailsUseCase(get())
    }

}