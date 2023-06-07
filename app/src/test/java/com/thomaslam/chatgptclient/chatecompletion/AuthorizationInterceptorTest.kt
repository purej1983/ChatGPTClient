package com.thomaslam.chatgptclient.chatecompletion

import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.interceptor.AuthorizationInterceptor
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import com.thomaslam.chatgptclient.chatecompletion.util.ChatGptConfigurationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After

import org.junit.Before
import org.junit.Test


class AuthorizationInterceptorTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var chatGptConfigurationProvider: ChatGptConfigurationProvider
    private lateinit var usecase: ConfigUseCase

    @Before
    fun setup(){
        mockServer = MockWebServer()
        mockServer.start()
        usecase = mockk(relaxed = true)
        chatGptConfigurationProvider = ChatGptConfigurationProvider(usecase)
        mockkObject(chatGptConfigurationProvider)
        every { chatGptConfigurationProvider.chatGptApiKey } returns "TestingApiKey"
    }

    @Test
    fun authorizationInterceptorTest() {

        val interceptor = AuthorizationInterceptor(chatGptConfigurationProvider)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val response = MockResponse()
            .setResponseCode(200)
            .setBody("Mock response")
        mockServer.enqueue(response)

        val request = Request.Builder()
            .url(mockServer.url("/"))
            .build()

        val result = client.newCall(request).execute()
        val authorizationValue = result.request.headers["Authorization"]
        assertEquals(authorizationValue, "Bearer TestingApiKey")


    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }
}