package com.gabriel_codarcea.mycontacts.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gabriel_codarcea.mycontacts.data.dao.ContactDao
import com.gabriel_codarcea.mycontacts.data.database.ContactsDatabase
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import com.gabriel_codarcea.mycontacts.test_helpers.getTestPagingSource
import com.gabriel_codarcea.mycontacts.test_helpers.testModule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

class ContactsViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var contactsDatabase: ContactsDatabase

    @MockK
    lateinit var contactDao: ContactDao

    private val contactsDatabaseRepository: ContactsDatabaseRepository  by inject()

    private lateinit var contactsViewModel: ContactsViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        startKoin { modules(testModule) }

        every { contactDao.getContactsPaginated() } returns getTestPagingSource()
        every { contactsDatabase.contactDao() } returns contactDao

        every { contactsDatabaseRepository.getContacts() } returns getTestPagingSource()

        contactsViewModel = ContactsViewModel()
    }

    @Test
    fun `test pagedList is filled properly when view model starts`() = runBlocking {
        contactsViewModel.pagedList.first()

        verify { contactsDatabaseRepository.getContacts() }
    }
}
