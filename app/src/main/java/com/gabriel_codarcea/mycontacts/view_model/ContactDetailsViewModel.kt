package com.gabriel_codarcea.mycontacts.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gabriel_codarcea.mycontacts.data.model.Contact
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class ContactDetailsViewModel(contactId: Int) : ViewModel(), KoinComponent {

    private val contactsDatabaseRepository: ContactsDatabaseRepository by inject()

    val contact: LiveData<Contact> =
        Transformations.map(contactsDatabaseRepository.getContactById(contactId)) {
            it
        }
}
