package com.gabriel_codarcea.mycontacts.api

import com.gabriel_codarcea.mycontacts.data.model.Contact

interface ApiResponseInterface<T> {
    fun onFailure(t: Throwable)
    fun onResponse(contacts: List<Contact>)
}