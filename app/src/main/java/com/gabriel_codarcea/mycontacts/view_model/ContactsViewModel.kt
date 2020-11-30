package com.gabriel_codarcea.mycontacts.view_model

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class ContactsViewModel : ViewModel(), KoinComponent {

    private val contactsDatabaseRepository: ContactsDatabaseRepository by inject()

    val pagedList = Pager(
        PagingConfig(
            pageSize = 40,
            enablePlaceholders = true,
            maxSize = 160
        )
    ) {
        contactsDatabaseRepository.getContacts()
    }.flow
}
