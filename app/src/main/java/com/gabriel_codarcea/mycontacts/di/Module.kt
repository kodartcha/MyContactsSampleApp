package com.gabriel_codarcea.mycontacts.di

import com.gabriel_codarcea.mycontacts.api.ApiClient
import com.gabriel_codarcea.mycontacts.data.database.ContactsDatabase
import com.gabriel_codarcea.mycontacts.data.manager.ContactsManager
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import com.gabriel_codarcea.mycontacts.utils.AvatarManager
import com.gabriel_codarcea.mycontacts.utils.SharedPreferencesManager
import com.gabriel_codarcea.mycontacts.view_model.ContactDetailsViewModel
import com.gabriel_codarcea.mycontacts.view_model.ContactsViewModel
import com.gabriel_codarcea.mycontacts.view_model.LauncherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val mainModule = module {
    viewModel { ContactsViewModel() }
    viewModel { LauncherViewModel() }
    viewModel { (contactId: Int) -> ContactDetailsViewModel(contactId) }
    single { SharedPreferencesManager(androidContext()) }
    single { ContactsDatabase.getDatabase(androidContext()) }
    single { ContactsDatabaseRepository(get()) }
    single { ContactsManager() }
    single { ApiClient }
    single { AvatarManager() }
    single { androidContext().contentResolver }
}
