package com.gabriel_codarcea.mycontacts.test_helpers

import android.graphics.Bitmap
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagingSource
import com.gabriel_codarcea.mycontacts.data.model.Contact
import java.io.ByteArrayOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal fun getContactTest1() =
    Contact(3, "Test Name 1", "12345678", "https://avatar.com/pic1.jpeg")

internal fun getContactTest2() =
    Contact(7, "Test Name 2", "90123456", "https://avatar.com/pic2.jpeg")

internal fun getContactTest3() =
    Contact(9, "Test Name 3", "78901234", "https://avatar.com/pic3.jpeg")

fun getTestPagingSource(): PagingSource<Int, Contact> = object : PagingSource<Int, Contact>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> =
        when (params) {
            is LoadParams.Refresh -> LoadResult.Page(
                data = listOf(getContactTest1(), getContactTest2(), getContactTest3()),
                prevKey = null,
                nextKey = null
            )
            else -> throw NotImplementedError("Test should fail if we get here")
        }
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 3,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

val CONTACTS_COLUMNS = listOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.Contacts.CONTENT_URI,
    ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
)

val PHONES_COLUMNS = listOf(
    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
    ContactsContract.CommonDataKinds.Phone.NUMBER
)

val PHOTO_COLUMNS = listOf(
    ContactsContract.Contacts.Photo.PHOTO
)

val CONTACT_1_ID = 1
val CONTACT_2_ID = 2
val CONTACT_3_ID = 3
val CONTACT_4_ID = 4
val CONTACT_5_ID = 5

val CONTACT_1_FULL = arrayOf(
    CONTACT_1_ID,
    "contact1",
    null,
    "photo"
)

val CONTACT_2_FULL = arrayOf(
    CONTACT_2_ID,
    "contact2",
    null,
    null
)
val CONTACT_3_EMPTY = arrayOf(
    CONTACT_3_ID,
    "contact3",
    null,
    null
)
val CONTACT_4_EMPTY = arrayOf(
    CONTACT_4_ID,
    "contact4",
    null,
    null
)
val CONTACT_5_FULL = arrayOf(
    CONTACT_5_ID,
    "contact5",
    null,
    null
)
val PHONE_CONTACT_1 = arrayOf(
    CONTACT_1_ID,
    "1113343431"
)
val PHONE_CONTACT_2_OK = arrayOf(
    CONTACT_2_ID,
    "9999999999"
)
val PHONE_CONTACT_3_EMPTY = arrayOf(
    CONTACT_3_ID,
    null
)
val PHONE_CONTACT_4_EMPTY = arrayOf(
    CONTACT_4_ID,
    null
)
val PHONE_CONTACT_5_OK = arrayOf(
    CONTACT_5_ID,
    "88888888888"
)

fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

