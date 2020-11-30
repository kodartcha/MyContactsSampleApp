package com.gabriel_codarcea.mycontacts.test_helpers

import android.app.Application
import android.content.ContentResolver
import android.content.SharedPreferences
import com.gabriel_codarcea.mycontacts.api.ApiClient
import com.gabriel_codarcea.mycontacts.data.manager.ContactsManager
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import com.gabriel_codarcea.mycontacts.utils.SharedPreferencesManager
import io.mockk.mockk
import org.koin.dsl.module

val testModule = module {
    single { mockk<SharedPreferencesManager>() }
    single { mockk<ContactsDatabaseRepository>() }
    single { mockk<ContactsManager>() }
    single { mockk<SharedPreferences>() }
    single { mockk<Application>() }
    single { mockk<ApiClient>() }
    single { mockk<ContentResolver>() }
}