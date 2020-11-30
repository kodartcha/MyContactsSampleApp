package com.gabriel_codarcea.mycontacts.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.gabriel_codarcea.mycontacts.R

class AvatarManager {
    fun loadAvatar(context: Context, url: String, view: ImageView) {
        Glide
            .with(context)
            .load(url)
            .fitCenter()
            .placeholder(R.drawable.ic_avatar_default)
            .into(view);
    }
}
