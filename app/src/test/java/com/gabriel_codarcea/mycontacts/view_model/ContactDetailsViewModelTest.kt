package com.gabriel_codarcea.mycontacts.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.gabriel_codarcea.mycontacts.data.dao.ContactDao
import com.gabriel_codarcea.mycontacts.data.database.ContactsDatabase
import com.gabriel_codarcea.mycontacts.data.model.Contact
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import com.gabriel_codarcea.mycontacts.test_helpers.getContactTest1
import com.gabriel_codarcea.mycontacts.test_helpers.testModule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.test.AutoCloseKoinTest

class ContactDetailsViewModelTest : AutoCloseKoinTest() {

    @Rule
    @JvmField
    val testRule = InstantTaskExecutorRule()

    @MockK
    lateinit var contactsDatabase: ContactsDatabase

    @MockK
    lateinit var contactDao: ContactDao

    private val contactsDatabaseRepository: ContactsDatabaseRepository by inject()

    private lateinit var contactDetailsViewModel: ContactDetailsViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        startKoin { modules(testModule) }

        every { contactDao.getContactById(3) } returns MutableLiveData(getContactTest1())
        every { contactsDatabase.contactDao() } returns contactDao

        every { contactsDatabaseRepository.getContactById(3) } returns MutableLiveData(
            getContactTest1()
        )

        contactDetailsViewModel = ContactDetailsViewModel(3)
    }

    @Test
    fun `test viewModel should load contact with specific ID on init`() {
        val expectedContact = getContactTest1()

        contactDetailsViewModel.contact.observeForever { }

        assertEquals(expectedContact, contactDetailsViewModel.contact.value)
    }
}