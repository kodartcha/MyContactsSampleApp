package com.gabriel_codarcea.mycontacts.data.manager

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.gabriel_codarcea.mycontacts.test_helpers.*
import io.mockk.MockKAnnotations
import io.mockk.every
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.fakes.RoboCursor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
class ContactsManagerTest : AutoCloseKoinTest() {

    private val application: Application by inject()

    private lateinit var contactsManager: ContactsManager

    private lateinit var allContactsRoboCursor: RoboCursor
    private lateinit var phoneNumbersRoboCursor: RoboCursor

    private val contentResolver: ContentResolver by inject()

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        startKoin { modules(testModule) }

        allContactsRoboCursor = RoboCursor()
        phoneNumbersRoboCursor = RoboCursor()

        every {
            contentResolver.query(
                Uri.parse("content://com.android.contacts/data/phones"),
                null,
                "contact_id=?",
                any(),
                null
            )
        } returns phoneNumbersRoboCursor
        every {
            contentResolver.query(
                Uri.parse("content://com.android.contacts/contacts"),
                null,
                null,
                null,
                null
            )
        } returns allContactsRoboCursor

        allContactsRoboCursor.setColumnNames(CONTACTS_COLUMNS as MutableList<String>?)
        phoneNumbersRoboCursor.setColumnNames(PHONES_COLUMNS)

        every { application.contentResolver } returns contentResolver

        contactsManager = ContactsManager()
    }

    @Test
    fun `get full contacts list from device`() {
        allContactsRoboCursor.setResults(
            arrayOf(
                CONTACT_1_FULL,
                CONTACT_2_FULL,
                CONTACT_3_EMPTY,
                CONTACT_4_EMPTY,
                CONTACT_5_FULL
            )
        )
        phoneNumbersRoboCursor.setResults(
            arrayOf(
                PHONE_CONTACT_1,
                PHONE_CONTACT_2_OK,
                PHONE_CONTACT_3_EMPTY,
                PHONE_CONTACT_4_EMPTY,
                PHONE_CONTACT_5_OK
            )
        )
        val contacts = contactsManager.getContactsFromDevice()
        assertNotNull(contacts)
        assertEquals(5, contacts.size)
        assertEquals("contact1", contacts[0].name)
        assertEquals("contact5", contacts[4].name)
    }

    @Test
    fun `get contacts without phones from device`() {
        allContactsRoboCursor.setResults(
            arrayOf(
                CONTACT_1_FULL,
                CONTACT_2_FULL,
                CONTACT_3_EMPTY,
                CONTACT_4_EMPTY
            )
        )
        phoneNumbersRoboCursor.setResults(arrayOf())
        val contacts = contactsManager.getContactsFromDevice()
        assertNotNull(contacts)
        assertEquals(4, contacts.size)
        assertEquals("contact1", contacts[0].name)
        assertEquals("contact4", contacts[3].name)
    }

    @Test
    fun `get device contact image`() {
        val photosRoboCursor = RoboCursor()
        val bitmapByteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        every {
            contentResolver.query(
                any(),
                any(),
                null,
                null,
                null
            )
        } returns photosRoboCursor
        photosRoboCursor.setColumnNames(PHOTO_COLUMNS)
        photosRoboCursor.setResults(
            arrayOf(
                arrayOf(bitmapByteArray)
            )
        )

        val photoBitmap = contactsManager.getDeviceContactImage(Uri.parse("photo"))
        val expectedBitmap = BitmapFactory.decodeStream(
            ByteArrayInputStream(bitmapByteArray)
        )

        assertNotNull(photoBitmap)
        assertEquals(
            getByteArrayFromBitmap(photoBitmap!!).contentToString(),
            getByteArrayFromBitmap(expectedBitmap).contentToString()
        )
    }

    @Test
    fun `get device contact image returns null`() {
        every {
            contentResolver.query(
                any(),
                any(),
                null,
                null,
                null
            )
        } returns null

        val photoBitmap = contactsManager.getDeviceContactImage(Uri.parse("photo"))
        assertNull(photoBitmap)
    }

}