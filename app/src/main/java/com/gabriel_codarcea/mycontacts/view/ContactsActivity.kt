package com.gabriel_codarcea.mycontacts.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.adapter.ContactsAdapter
import com.gabriel_codarcea.mycontacts.adapter.RecyclerViewItemClickListenerInterface
import com.gabriel_codarcea.mycontacts.databinding.ActivityContactsBinding
import com.gabriel_codarcea.mycontacts.view_model.ContactsViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class ContactsActivity : AppCompatActivity() {

    private val viewModel by viewModel<ContactsViewModel>()
    private lateinit var binding: ActivityContactsBinding
    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_contacts
        )
        adapter = ContactsAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter.setOnItemClickListener(object : RecyclerViewItemClickListenerInterface {
            override fun onItemClick(contactId: Int) {
                goToContactDetail(contactId)
            }
        })
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.pagedList.collectLatest { adapter.submitData(it) }
        }
    }

    private fun goToContactDetail(id: Int) {
        val contactDetailIntent = Intent(
            this@ContactsActivity,
            ContactDetailsActivity::class.java
        )
        contactDetailIntent.putExtra(resources.getString(R.string.contact_id), id)
        startActivity(contactDetailIntent)
    }
}
