package com.example.serversimulator

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

private const val SLOW_NETWORK_STEPS = 20
private const val SLOW_NETWORK_STEP_MS = 100L
private const val HTTP_SERVICE_UNAVAILABLE = 503

// Для проверки: каждый запрос идёт около 2 секунд. Включается флагом в ServerApp
val slowNetwork = Interceptor { chain ->
    repeat(SLOW_NETWORK_STEPS) {
        if (chain.call().isCanceled()) throw IOException("Canceled")
        Thread.sleep(SLOW_NETWORK_STEP_MS)
    }
    chain.proceed(chain.request())
}

// Для проверки: сервер всегда отвечает 503. Включается флагом в ServerApp
val fakeServerError = Interceptor { chain ->
    Response.Builder()
        .request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(HTTP_SERVICE_UNAVAILABLE)
        .message("Service Unavailable")
        .body("".toResponseBody("text/plain".toMediaType()))
        .build()
}