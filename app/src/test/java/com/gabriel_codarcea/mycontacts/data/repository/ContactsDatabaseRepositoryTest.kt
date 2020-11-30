package com.gabriel_codarcea.mycontacts.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.gabriel_codarcea.mycontacts.data.dao.ContactDao
import com.gabriel_codarcea.mycontacts.data.database.ContactsDatabase
import com.gabriel_codarcea.mycontacts.data.model.Contact
import com.gabriel_codarcea.mycontacts.test_helpers.getContactTest1
import com.gabriel_codarcea.mycontacts.test_helpers.getContactTest2
import com.gabriel_codarcea.mycontacts.test_helpers.getContactTest3
import com.gabriel_codarcea.mycontacts.test_helpers.getTestPagingSource
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.AutoCloseKoinTest

class ContactsDatabaseRepositoryTest: AutoCloseKoinTest() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var contactsDatabase: ContactsDatabase

    @MockK
    lateinit var contactDao: ContactDao

    lateinit var contactsDatabaseRepository: ContactsDatabaseRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every { contactsDatabase.contactDao() } returns contactDao

        contactsDatabaseRepository = ContactsDatabaseRepository(contactsDatabase)
    }

    @Test
    fun `test insertContacts propagates a list of Contacts to contactsDao`() {
        val list = listOf(getContactTest1(), getContactTest2(), getContactTest3())

        every { contactDao.insertContacts(any()) } just runs

        contactsDatabaseRepository.insertContacts(list)

        verify { contactDao.insertContacts(list) }
    }

    @Test
    fun `test getContacts returns the list of all available contacts from contactDao`() {
        val expectedDataSource = getTestPagingSource()

        every { contactDao.getContactsPaginated() } returns expectedDataSource

        val result = contactsDatabaseRepository.getContacts()

        verify { contactDao.getContactsPaginated() }
        assertEquals(expectedDataSource, result)
    }

    @Test
    fun `test getContactById returns the proper object from the contactDao`() {
        val expectedContact = Contact(7, "Test Name 2", "90123456", "https://avatar.com/pic2.jpeg")

        every { contactDao.getContactById(7) } returns MutableLiveData(getContactTest2())

        val result = contactsDatabaseRepository.getContactById(7)

        verify { contactDao.getContactById(7) }
        assertNotNull(result.value)
        assertEquals(result.value, expectedContact)
    }
}