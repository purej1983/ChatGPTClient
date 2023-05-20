package com.thomaslam.chatgptclient.chatecompletion.domain.entity

import com.google.gson.annotations.SerializedName

enum class Model(val modelName: String) {
    @SerializedName("gpt-4")
    GPT4("gpt-4"),

    @SerializedName("gpt-4-0314")
    GPT4_0314("gpt-4-0314"),

    @SerializedName("gpt-4-32k")
    GPT4_32k("gpt-4-32k"),

    @SerializedName("gpt-4-32k-0314")
    GPT4_32k_0314("gpt-4-32k-0314"),

    @SerializedName("gpt-3.5-turbo")
    GPT3_pt_5_turbo("gpt-3.5-turbo"),

    @SerializedName("gpt-3.5-turbo-0301")
    GPT3_pt_5_turbo_0301("gpt-3.5-turbo-0301")
}