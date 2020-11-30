package com.gabriel_codarcea.mycontacts.data.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.core.database.getStringOrNull
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.api.ApiClient
import com.gabriel_codarcea.mycontacts.api.ApiResponseInterface
import com.gabriel_codarcea.mycontacts.api.ApiService
import com.gabriel_codarcea.mycontacts.api.model.RMResponse
import com.gabriel_codarcea.mycontacts.data.model.Contact
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream

class ContactsManager : KoinComponent {

    private val context: Application by inject()
    private val apiClient: ApiClient by inject()

    private var rmContactsList = mutableListOf<Contact>()

    private val contentResolver: ContentResolver by inject()

    fun getContactsFromDevice(): List<Contact> {
        val contactList = mutableListOf<Contact>()

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        cursor?.let {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    val phoneNumber = getPhoneNumber(id)

                    val contactUri: Uri =
                        ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI,
                            id.toLong()
                        )
                    val photoUri: Uri =
                        Uri.withAppendedPath(
                            contactUri,
                            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
                        )

                    val newContact = Contact(0, name, phoneNumber, photoUri.toString())
                    contactList.add(newContact)
                }
            } // ELSE - No contacts available
        }
        cursor?.close()

        return contactList
    }

    private fun getPhoneNumber(id: String): String {
        var phoneNumber = ""
        val cursorPhone = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
            arrayOf(id),
            null
        )

        if (cursorPhone != null &&
            cursorPhone.count > 0 &&
            cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) != -1
        ) {
            cursorPhone.moveToNext()
            val columnIndex = cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            phoneNumber = cursorPhone.getStringOrNull(columnIndex) ?: ""
        }
        cursorPhone?.close()
        return phoneNumber
    }

    @SuppressLint("Recycle")
    fun getDeviceContactImage(uri: Uri): Bitmap? {
        val cursor: Cursor = context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.Contacts.Photo.PHOTO),
            null,
            null,
            null
        ) ?: return null

        var bitmap: Bitmap? = null

        cursor.use {
            if (it.moveToFirst()) {
                val data: ByteArray? = it.getBlob(0)
                if (data != null) {
                    bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(data))
                }
            }
        }
        cursor.close()
        return bitmap
    }

    fun downloadRMContactsByPage(page: Int, callback: ApiResponseInterface<List<Contact>>?) {
        val service = apiClient.getClient()
        rmContactsList = mutableListOf()
        service?.let {
            getRMContacts(it, page, callback)
        }
    }

    private fun getRMContacts(
        service: ApiService,
        page: Int,
        callback: ApiResponseInterface<List<Contact>>?
    ) {
        service.getCharacters(page).enqueue(object : Callback<RMResponse> {
            override fun onFailure(
                call: Call<RMResponse>,
                t: Throwable
            ) {
                callback?.onFailure(t)
            }

            override fun onResponse(
                call: Call<RMResponse>,
                response: Response<RMResponse>
            ) {
                val rmResponse = response.body()
                if (rmResponse != null) {
                    rmContactsList.addAll(rmResponse.results.map {
                        Contact(
                            0,
                            it.name,
                            "",
                            it.image
                        )
                    })

                    if (rmResponse.info.next == null) {
                        callback?.onResponse(rmContactsList)
                    } else {
                        getRMContacts(service, page + 1, callback)
                    }
                } else {
                    callback?.onFailure(
                        Throwable(
                            context.resources.getString(R.string.cm_empty_contacts),
                            null
                        )
                    )
                }
            }
        })
    }
}