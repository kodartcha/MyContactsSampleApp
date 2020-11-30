package com.gabriel_codarcea.mycontacts.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus.Companion.DOWNLOADING
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus.Companion.FAILED
import com.gabriel_codarcea.mycontacts.test_helpers.testModule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

class SharedPreferencesManagerTest : AutoCloseKoinTest() {

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var resources: Resources

    @MockK
    lateinit var editor: SharedPreferences.Editor

    private val sharedPreferences: SharedPreferences by inject()

    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val downloadKey = "download_state"
    private val sharedKey = "my_contacts_shared_preferences"

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        startKoin { modules(testModule) }

        every { context.resources } returns resources
        every { resources.getString(R.string.shared_preferences) } returns sharedKey
        every {
            context.getSharedPreferences(
                sharedKey,
                Context.MODE_PRIVATE
            )
        } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor

        sharedPreferencesManager = SharedPreferencesManager(context)
    }

    @Test
    fun `test saveState saves the value in editor`() {
        every { resources.getString(R.string.sp_download_state) } returns downloadKey
        every { sharedPreferences.getInt(downloadKey, 0) } returns DOWNLOADING

        assertEquals(DOWNLOADING, sharedPreferencesManager.getState())
    }

    @Test
    fun `test getState returns the value from sharedPreferences`() {
        every { resources.getString(R.string.sp_download_state) } returns downloadKey
        every { editor.putInt(downloadKey, FAILED) } returns editor
        every { editor.apply() } just runs

        sharedPreferencesManager.saveState(FAILED)

        verify { editor.putInt(downloadKey, FAILED) }
        verify { editor.apply() }
    }
}
