package com.gabriel_codarcea.mycontacts.utils

import androidx.recyclerview.widget.DiffUtil
import com.gabriel_codarcea.mycontacts.data.model.Contact

class ContactDiffUtilItemCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}
