package com.example.serversimulator

// Настройки сети и повторов в одном месте
object NetworkConfig {
    const val BASE_URL = "https://api.open-meteo.com/"
    const val CALL_TIMEOUT_SECONDS = 10L
    const val RETRY_ATTEMPTS = 3
    const val RETRY_INITIAL_DELAY_MS = 500L
    const val RETRY_DELAY_FACTOR = 2

    const val HTTP_SERVER_ERROR = 500
}