package com.gabriel_codarcea.mycontacts.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabriel_codarcea.mycontacts.data.model.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(contacts: List<Contact>)

    @Query("SELECT * FROM contact ORDER BY name")
    fun getContactsPaginated(): PagingSource<Int, Contact>

    @Query("SELECT * FROM contact WHERE id=:id")
    fun getContactById(id: Int): LiveData<Contact>
}
