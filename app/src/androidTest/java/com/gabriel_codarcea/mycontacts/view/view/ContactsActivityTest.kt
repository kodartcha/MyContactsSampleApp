package com.gabriel_codarcea.mycontacts.view.view

import android.app.Activity
import android.content.Intent
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.view.ContactsActivity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
class ContactsActivityTest {

    @Rule
    @JvmField
    val testRule = CountingTaskExecutorRule()

    @Test
    @Throws(InterruptedException::class, TimeoutException::class)
    fun showSomeResults() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ContactsActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity: Activity =
            InstrumentationRegistry.getInstrumentation().startActivitySync(intent)
        testRule.drainTasks(10, TimeUnit.SECONDS)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val recyclerView: RecyclerView = activity.findViewById(R.id.recycler_view)
        waitForAdapterChange(recyclerView)
        assertThat(recyclerView.adapter, notNullValue())
        waitForAdapterChange(recyclerView)
        assertThat(recyclerView.adapter!!.itemCount > 0, `is`(true))
    }

    @Throws(InterruptedException::class)
    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            recyclerView.adapter!!.registerAdapterDataObserver(
                object : AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        latch.countDown()
                    }

                    override fun onChanged() {
                        latch.countDown()
                    }
                })
        }
        if (recyclerView.adapter!!.itemCount > 0) {
            return  //already loaded
        }
        assertThat(latch.await(10, TimeUnit.SECONDS), `is`(true))
    }
}