package com.gabriel_codarcea.mycontacts.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "number") val number: String?,
    @ColumnInfo(name = "avatar") val avatar: String?
)
