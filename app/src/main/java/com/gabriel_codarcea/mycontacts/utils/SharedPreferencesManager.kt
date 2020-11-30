package com.gabriel_codarcea.mycontacts.utils

import android.content.Context
import android.content.SharedPreferences
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus

open class SharedPreferencesManager(private val context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        context.resources.getString(R.string.shared_preferences),
        Context.MODE_PRIVATE
    )

    fun saveState(state: Int) {
        with(sharedPref.edit()) {
            putInt(context.resources.getString(R.string.sp_download_state), state)
            apply()
        }
    }

    fun getState(): Int = sharedPref.getInt(
        context.resources.getString(R.string.sp_download_state),
        DownloadStatus.EMPTY
    )
}
