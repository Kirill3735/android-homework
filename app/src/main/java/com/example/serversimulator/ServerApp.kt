package com.example.serversimulator

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val HTTP_LOG_TAG = "ServerHttp"

// Флаги для проверки работы с сетью. В обычной работе оба выключены
private const val SIMULATE_SLOW_NETWORK = false
private const val SIMULATE_SERVER_ERROR = false

class ServerApp : Application() {

    private val json = Json { ignoreUnknownKeys = true }

    private val isDebuggable: Boolean
        get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private val client by lazy {
        OkHttpClient.Builder()
            .callTimeout(NetworkConfig.CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                if (isDebuggable) {
                    addInterceptor(
                        HttpLoggingInterceptor { Log.d(HTTP_LOG_TAG, it) }.apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )
                }
                if (SIMULATE_SLOW_NETWORK) addInterceptor(slowNetwork)
                if (SIMULATE_SERVER_ERROR) addInterceptor(fakeServerError)
            }
            .build()
    }

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WeatherApi::class.java)
    }

    // Один репозиторий на всё приложение, создаётся при первом обращении
    val repository by lazy { ServerRepository(api) }
}