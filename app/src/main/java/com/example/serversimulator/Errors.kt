package com.example.serversimulator

// Не удалось получить температуру снаружи
class WeatherLoadException(val canRetry: Boolean, cause: Throwable) : Exception(cause)