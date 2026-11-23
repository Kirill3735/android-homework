package com.example.serversimulator

import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

// Повторяет запрос при временных сбоях: нет сети, тайм-аут
// Попыток конечное число, последняя ошибка уходит наверх
suspend fun <T> retryIO(
    times: Int = NetworkConfig.RETRY_ATTEMPTS,
    initialDelay: Long = NetworkConfig.RETRY_INITIAL_DELAY_MS,
    block: suspend () -> T,
): T {
    var pause = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: IOException) {
        } catch (e: HttpException) {
            if (e.code() < NetworkConfig.HTTP_SERVER_ERROR) throw e
        }
        delay(pause.milliseconds)
        pause *= NetworkConfig.RETRY_DELAY_FACTOR
    }
    return block()
}