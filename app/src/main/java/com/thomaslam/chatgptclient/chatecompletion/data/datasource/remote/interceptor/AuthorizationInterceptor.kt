package com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.interceptor

import com.thomaslam.chatgptclient.chatecompletion.util.MyAppConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(
    private val myAppConfig: MyAppConfig
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Authorization", "Bearer " + myAppConfig.getChatGptApiKey())
            .build()
        return chain.proceed(request)
    }
}