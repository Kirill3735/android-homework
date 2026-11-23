package com.example.serversimulator

import androidx.compose.ui.graphics.Color

// Цвета состояний
object ServerColors {
    val Warning = Color(0xFFE65100)
    val Error = Color(0xFFB71C1C)

    private val Offline = Color(0xFFEEEEEE)
    private val Running = Color(0xFFE8F5E9)
    private val Overloaded = Color(0xFFFFF3E0)
    private val Crashed = Color(0xFFFFEBEE)

    fun backgroundFor(status: ServerStatus): Color = when (status) {
        ServerStatus.OFFLINE -> Offline
        ServerStatus.RUNNING -> Running
        ServerStatus.OVERLOADED -> Overloaded
        ServerStatus.CRASHED -> Crashed
    }

    fun labelFor(status: ServerStatus): Color = when (status) {
        ServerStatus.OVERLOADED -> Warning
        ServerStatus.CRASHED -> Error
        ServerStatus.OFFLINE, ServerStatus.RUNNING -> Color.Unspecified
    }
}