package com.thomaslam.chatgptclient.chatecompletion.data.remote.interceptor

import com.thomaslam.chatgptclient.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request?.newBuilder()
            ?.addHeader("Authorization", "Bearer " +BuildConfig.CHAT_GPT_API_KEY)
            ?.build()
        return chain.proceed(request)
    }
}