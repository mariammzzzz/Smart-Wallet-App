package com.mjapa21

import android.app.Application
import com.mjapa21.smartwallet.di.dataModule
import com.mjapa21.smartwallet.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SmartWalletApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SmartWalletApplication)
            modules(viewModelsModule, dataModule)
        }
    }

}