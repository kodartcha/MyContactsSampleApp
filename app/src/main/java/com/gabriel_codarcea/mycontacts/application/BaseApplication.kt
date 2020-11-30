package com.gabriel_codarcea.mycontacts.application

import android.app.Application
import com.gabriel_codarcea.mycontacts.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(mainModule)
        }
    }
}
