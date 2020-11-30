package com.gabriel_codarcea.mycontacts.adapter

import android.app.Application
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.data.manager.ContactsManager
import com.gabriel_codarcea.mycontacts.databinding.ContactListItemBinding
import com.gabriel_codarcea.mycontacts.data.model.Contact
import com.gabriel_codarcea.mycontacts.utils.AvatarManager
import com.gabriel_codarcea.mycontacts.utils.ContactDiffUtilItemCallback
import org.koin.core.KoinComponent
import org.koin.core.inject

class ContactsAdapter :
    PagingDataAdapter<Contact, ContactViewHolder>(ContactDiffUtilItemCallback()),
    KoinComponent {

    private val context: Application by inject()
    private val avatarManager: AvatarManager by inject()
    private val contactsManager: ContactsManager by inject()

    private lateinit var onClickListener: RecyclerViewItemClickListenerInterface

    fun setOnItemClickListener(listener: RecyclerViewItemClickListenerInterface) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = DataBindingUtil.inflate<ContactListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.contact_list_item,
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        if (contact == null) {
            holder.binding.name.text =
                holder.binding.root.context.getString(R.string.la_loading_contacts)
            holder.binding.avatar.setImageResource(R.drawable.ic_avatar_default)
        } else {
            holder.binding.name.text = contact.name
            if(contact.avatar.isNullOrEmpty()){
                holder.binding.avatar.setImageResource(R.drawable.ic_avatar_default)
            }else{
                if(!contact.number.isNullOrEmpty()){
                    contactsManager.getDeviceContactImage(Uri.parse(contact.avatar))?.let {
                        holder.binding.avatar.setImageBitmap(it)
                    } ?: holder.binding.avatar.setImageResource(R.drawable.ic_avatar_default)
                }else {
                    avatarManager.loadAvatar(context, contact.avatar, holder.binding.avatar)
                }
            }
            holder.itemView.setOnClickListener {
                onClickListener.let {
                    onClickListener.onItemClick(contact.id)
                }
            }
        }
    }
}
