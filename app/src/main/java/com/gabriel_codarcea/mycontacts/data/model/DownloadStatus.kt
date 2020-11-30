package com.gabriel_codarcea.mycontacts.data.model

class DownloadStatus(var state: Int? = EMPTY) {
    companion object {
        val EMPTY = 0
        val DOWNLOADING = 1
        val FINISHED = 2
        val FAILED = 3
    }
}
