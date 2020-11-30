package com.gabriel_codarcea.mycontacts.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gabriel_codarcea.mycontacts.api.ApiResponseInterface
import com.gabriel_codarcea.mycontacts.data.dao.ContactDao
import com.gabriel_codarcea.mycontacts.data.database.ContactsDatabase
import com.gabriel_codarcea.mycontacts.data.manager.ContactsManager
import com.gabriel_codarcea.mycontacts.data.model.Contact
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus.Companion.FINISHED
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import com.gabriel_codarcea.mycontacts.test_helpers.getContactTest1
import com.gabriel_codarcea.mycontacts.test_helpers.getOrAwaitValue
import com.gabriel_codarcea.mycontacts.test_helpers.testModule
import com.gabriel_codarcea.mycontacts.utils.SharedPreferencesManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

class LauncherViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ObsoleteCoroutinesApi
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @MockK
    lateinit var contactsDatabase: ContactsDatabase

    @MockK
    lateinit var contactDao: ContactDao

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private val contactsManager: ContactsManager by inject()
    private val contactsDatabaseRepository: ContactsDatabaseRepository by inject()

    private lateinit var launcherViewModel: LauncherViewModel

    private val callbackSlot = slot<ApiResponseInterface<List<Contact>>>()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockKAnnotations.init(this)

        startKoin { modules(testModule) }

        Dispatchers.setMain(mainThreadSurrogate)

        every { sharedPreferencesManager.getState() } returns DownloadStatus.EMPTY
        every { sharedPreferencesManager.saveState(any()) } just runs
        every { contactDao.insertContacts(any()) } just runs
        every { contactsDatabase.contactDao() } returns contactDao
        every { contactsManager.getContactsFromDevice() } returns listOf(getContactTest1())

        launcherViewModel = LauncherViewModel()
    }

    @ExperimentalCoroutinesApi
    @After
    fun teardown(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `test downloadContacts from device and Rick and Morty API into local DB succeeds`() =
        runBlocking {
            assertNotNull(launcherViewModel.downloadContactsStatus.value)
            assertEquals(
                launcherViewModel.downloadContactsStatus.value!!.state,
                DownloadStatus.EMPTY
            )

            every {
                contactsManager.downloadRMContactsByPage(
                    any(),
                    capture(callbackSlot)
                )
            } answers { callbackSlot.captured.onResponse(listOf(getContactTest1())) }
            every { contactsDatabaseRepository.insertContacts(any()) } just runs

            launcherViewModel.downloadContacts()

            assertNotNull(launcherViewModel.downloadContactsStatus.value)
            assertEquals(
                launcherViewModel.downloadContactsStatus.value!!.state,
                DownloadStatus.DOWNLOADING
            )

            verify { contactsManager.getContactsFromDevice() }
            verify { contactsManager.downloadRMContactsByPage(any(), any()) }
            verify { contactsDatabaseRepository.insertContacts(any()) }
            verify { sharedPreferencesManager.saveState(FINISHED) }

            assertEquals(
                launcherViewModel.downloadContactsStatus.value!!.state, FINISHED
            )
        }

    @Test
    fun `test downloadContacts from device and Rick and Morty API into local DB fails`() =
        runBlocking {
            assertNotNull(launcherViewModel.downloadContactsStatus.value)
            assertEquals(
                launcherViewModel.downloadContactsStatus.value!!.state,
                DownloadStatus.EMPTY
            )

            every {
                contactsManager.downloadRMContactsByPage(
                    any(),
                    capture(callbackSlot)
                )
            } answers { callbackSlot.captured.onFailure(Throwable("Error", null)) }

            launcherViewModel.downloadContacts()
            Thread.sleep(1000)

            verify { contactsManager.getContactsFromDevice() }
            verify { contactsManager.downloadRMContactsByPage(any(), any()) }

            val expected = launcherViewModel.downloadContactsStatus.getOrAwaitValue()

            assertEquals(
                expected.state,
                DownloadStatus.FAILED
            )
        }
}
