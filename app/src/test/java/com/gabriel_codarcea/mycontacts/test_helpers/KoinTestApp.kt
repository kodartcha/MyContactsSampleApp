package com.gabriel_codarcea.mycontacts.test_helpers

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

class KoinTestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@KoinTestApp)
            modules(emptyList())
        }
    }

    fun loadModule(module: Module) {
        loadModules(listOf(module))
    }

    fun loadModules(modules: List<Module>) {
        loadKoinModules(modules)
    }

    fun unloadModule(module: Module) {
        unloadModules(listOf(module))
    }

    fun unloadModules(modules: List<Module>) {
        unloadKoinModules(modules)
    }
}