package com.gabriel_codarcea.mycontacts.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gabriel_codarcea.mycontacts.api.ApiResponseInterface
import com.gabriel_codarcea.mycontacts.data.manager.ContactsManager
import com.gabriel_codarcea.mycontacts.data.model.Contact
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus.Companion.DOWNLOADING
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus.Companion.FAILED
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus.Companion.FINISHED
import com.gabriel_codarcea.mycontacts.data.repository.ContactsDatabaseRepository
import com.gabriel_codarcea.mycontacts.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class LauncherViewModel : ViewModel(), KoinComponent {

    private val sharedPrefs: SharedPreferencesManager by inject()
    private val contactsRepo: ContactsDatabaseRepository by inject()
    private val contactsManager: ContactsManager by inject()

    private val initPage = 1

    val downloadContactsStatus =
        MutableLiveData<DownloadStatus>().apply { value = DownloadStatus(sharedPrefs.getState()) }

    fun downloadContacts() {
        downloadContactsStatus.postValue(DownloadStatus(DOWNLOADING))
        CoroutineScope(IO).launch {

            // Get contacts from device
            val deviceContacts = contactsManager.getContactsFromDevice()

            // Download contacts from RM Api
            contactsManager.downloadRMContactsByPage(
                initPage,
                object : ApiResponseInterface<List<Contact>> {
                    override fun onFailure(t: Throwable) {
                        // Download failed, show error dialog with retry
                        downloadContactsStatus.postValue(DownloadStatus(FAILED))
                    }

                    override fun onResponse(
                        contacts: List<Contact>
                    ) {
                        // Download succeeded, save all into db
                        CoroutineScope(IO).launch {

                            // Insert device contacts into local database
                            contactsRepo.insertContacts(deviceContacts)

                            // Insert Rick & Morty contacts into local database
                            contactsRepo.insertContacts(contacts)

                            // Save and update download state
                            withContext(Main) {
                                sharedPrefs.saveState(FINISHED)
                                downloadContactsStatus.postValue(DownloadStatus(FINISHED))
                            }
                        }
                    }
                })
        }
    }
}
