package com.gabriel_codarcea.mycontacts.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.gabriel_codarcea.mycontacts.data.dao.ContactDao
import com.gabriel_codarcea.mycontacts.data.database.ContactsDatabase
import com.gabriel_codarcea.mycontacts.data.model.Contact

class ContactsDatabaseRepository(contactsDatabase: ContactsDatabase) {

    private val contactDao: ContactDao = contactsDatabase.contactDao()

    fun insertContacts(contacts: List<Contact>) {
        contactDao.insertContacts(contacts)
    }

    fun getContacts(): PagingSource<Int, Contact> = contactDao.getContactsPaginated()

    fun getContactById(id: Int): LiveData<Contact> = contactDao.getContactById(id)
}