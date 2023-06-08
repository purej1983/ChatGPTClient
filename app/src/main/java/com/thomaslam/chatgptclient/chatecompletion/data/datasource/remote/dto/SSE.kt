package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.dto

class SSE(private var data: String?) {
    private val DONE_DATA = "[DONE]"


    fun getData(): String? {
        return data
    }

    fun toBytes(): ByteArray {
        return String.format("data: %s\n\n", data).toByteArray()
    }

    fun isDone(): Boolean {
        return DONE_DATA.equals(data, ignoreCase = true)
    }
}