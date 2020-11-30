package com.gabriel_codarcea.mycontacts.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabriel_codarcea.mycontacts.data.dao.ContactDao
import com.gabriel_codarcea.mycontacts.data.model.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class ContactsDatabase : RoomDatabase(){

    abstract fun contactDao(): ContactDao

    companion object {
        private const val DB_NAME = "my_contacts.db"

        private var instance: ContactsDatabase? = null

        fun getDatabase(context: Context): ContactsDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactsDatabase::class.java,
                    DB_NAME
                ).build()
            }
            return instance!!
        }
    }

}