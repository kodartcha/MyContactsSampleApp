package com.gabriel_codarcea.mycontacts.test_helpers

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.koin.core.module.Module
import org.koin.test.AutoCloseKoinTest

open class CustomAutoCloseKoinTest(private val modules: List<Module> = listOf()) : AutoCloseKoinTest() {

    lateinit var app: KoinTestApp

    @Before
    fun init() {
        app = ApplicationProvider.getApplicationContext() as KoinTestApp
        app.loadModules(modules)
    }

    @After
    fun finish() {
        app.unloadModules(modules)
    }
}