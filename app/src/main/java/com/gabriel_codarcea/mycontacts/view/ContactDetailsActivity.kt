package com.gabriel_codarcea.mycontacts.view

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.data.manager.ContactsManager
import com.gabriel_codarcea.mycontacts.databinding.ActivityContactDetailBinding
import com.gabriel_codarcea.mycontacts.utils.AvatarManager
import com.gabriel_codarcea.mycontacts.view_model.ContactDetailsViewModel
import kotlinx.android.synthetic.main.activity_contact_detail.*
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class ContactDetailsActivity : AppCompatActivity(), KoinComponent {

    private val contactId: Int by lazy {
        intent.getIntExtra(
            resources.getString(R.string.contact_id),
            0
        )
    }
    private val viewModel by viewModel<ContactDetailsViewModel> { parametersOf(contactId) }
    private val avatarManager: AvatarManager by inject()
    private val contactsManager: ContactsManager by inject()

    private lateinit var binding: ActivityContactDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        initObservers()
    }

    private fun initObservers() {
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_contact_detail
        )

        viewModel.contact.observe(this@ContactDetailsActivity, {
            detail_name.text = it.name
            if (!it.number.isNullOrEmpty()) {
                phone_content.text = it.number
            } else {
                phone_content.text = resources.getString(R.string.detail_phone_unknown)
            }
            if (it.avatar.isNullOrEmpty()) {
                detail_avatar.setImageResource(R.drawable.ic_avatar_default)
            } else {
                if (!it.number.isNullOrEmpty()) {
                    contactsManager.getDeviceContactImage(Uri.parse(it.avatar))?.let { bm ->
                        detail_avatar.setImageBitmap(bm)
                    } ?: detail_avatar.setImageResource(R.drawable.ic_avatar_default)
                } else {
                    avatarManager.loadAvatar(this, it.avatar, detail_avatar)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.onBackPressed()
        }
        return true
    }
}
