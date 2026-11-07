package com.example.serversimulator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ServerRepository {

    var servers by mutableStateOf(startServers())
        private set

    private var nextId = servers.maxOf { it.id } + 1

    fun server(id: Int): Server? = servers.find { it.id == id }

    fun add() {
        servers = listOf(Server(id = nextId++)) + servers
    }

    fun remove(id: Int) {
        servers = servers.filterNot { it.id == id }
    }

    fun rename(id: Int, name: String) = update(id) { it.rename(name) }

    fun start(id: Int) = update(id) { it.start() }

    fun addLoad(id: Int) = update(id) { it.addLoad() }

    fun coolDown(id: Int) = update(id) { it.coolDown() }

    fun updateVersion(id: Int) = update(id) { it.updateVersion() }

    fun reset(id: Int) = update(id) { it.reset() }

    private fun update(id: Int, transform: (Server) -> Server) {
        servers = servers.map { if (it.id == id) transform(it) else it }
    }
}

fun startServers(): List<Server> {
    val scenarios: List<(Server) -> Server> = listOf(
        { it },
        { it.start() },
        { it.start().addLoad() },
        { it.start().addLoad().addLoad() },
        { it.start().addLoad().addLoad().addLoad() },
        { it.start().addLoad().addLoad().addLoad().coolDown() },
        { it.start().addLoad().addLoad().addLoad().coolDown().coolDown() },
        { it.start().updateVersion() },
        { it.start().updateVersion().updateVersion() },
        { it.start().updateVersion().updateVersion().updateVersion() },
        { it.start().addLoad().addLoad().coolDown() },
        { it }
    )
    return scenarios.mapIndexed { index, scenario -> scenario(Server(id = index + 1)) }
}