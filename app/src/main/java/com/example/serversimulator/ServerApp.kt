package com.example.serversimulator

import android.app.Application

class ServerApp : Application() {
    val repository = ServerRepository()
}