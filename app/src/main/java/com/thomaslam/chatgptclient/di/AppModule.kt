package com.thomaslam.chatgptclient.di

import android.app.Application
import androidx.room.Room
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGPTDatabase
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.ChatCompletionService
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.remote.interceptor.AuthorizationInterceptor
import com.thomaslam.chatgptclient.chatecompletion.data.repository.ChatCompletionRepositoryImpl
import com.thomaslam.chatgptclient.chatecompletion.data.repository.ChatGptConfigRepositoryImpl
import com.thomaslam.chatgptclient.chatecompletion.domain.ChatCompletionUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatCompletionRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.repository.ChatGptConfigRepository
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.ConfigUseCase
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.GetConfig
import com.thomaslam.chatgptclient.chatecompletion.domain.use_case.SaveConfig
import com.thomaslam.chatgptclient.chatecompletion.util.ChatGptConfigurationProvider
import com.thomaslam.chatgptclient.chatecompletion.util.ConfigurationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun provideAuthorizationInterceptor(chatGptConfigurationProvider: ChatGptConfigurationProvider): AuthorizationInterceptor = AuthorizationInterceptor(chatGptConfigurationProvider)
    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, authorizationInterceptor: AuthorizationInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authorizationInterceptor)
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openai.com")
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideChatCompletionService(retrofit: Retrofit): ChatCompletionService = retrofit.create(
        ChatCompletionService::class.java)

    @Singleton
    @Provides
    fun provideChatCompletionRepository(db: ChatGPTDatabase, chatCompletionService: ChatCompletionService, configProvider: ConfigurationProvider): ChatCompletionRepository {
        return ChatCompletionRepositoryImpl(db.dao, chatCompletionService, configProvider)
    }

    @Singleton
    @Provides
    fun provideChatCompletionUseCase(chatCompletionRepository: ChatCompletionRepository): ChatCompletionUseCase {
        return ChatCompletionUseCase(chatCompletionRepository)
    }

    @Singleton
    @Provides
    fun provideChatGptConfigRepository(db: ChatGPTDatabase): ChatGptConfigRepository {
        return ChatGptConfigRepositoryImpl(db.dao)
    }

    @Singleton
    @Provides
    fun provideConfigUseCase(repository: ChatGptConfigRepository): ConfigUseCase {
        return ConfigUseCase(
            getConfig = GetConfig(repository = repository),
            saveConfig = SaveConfig(repository = repository)
        )
    }

    @Provides
    @Singleton
    fun provideChatGPTDatabase(app: Application): ChatGPTDatabase {
        return Room
            .databaseBuilder(
                app,
                ChatGPTDatabase::class.java,
                ChatGPTDatabase.DATABASE_NAME
            )
            .addMigrations(ChatGPTDatabase.migration3To4)
            .build()
    }

    @Provides
    @Singleton
    fun provideMyAppConfig(useCase: ConfigUseCase): ConfigurationProvider {
        return ChatGptConfigurationProvider(useCase)
    }
}
