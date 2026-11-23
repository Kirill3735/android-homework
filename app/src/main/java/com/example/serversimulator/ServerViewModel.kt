package com.example.serversimulator

import androidx.lifecycle.ViewModel

data class ServerUiState(
    val server: Server?
)

class ServerViewModel(
    private val serverId: Int,
    private val repo: ServerRepository
) : ViewModel() {

    val uiState: ServerUiState
        get() = ServerUiState(server = repo.server(serverId))

    fun onRename(name: String) = repo.rename(serverId, name)

    fun onStart() = repo.start(serverId)

    fun onAddLoad() = repo.addLoad(serverId)

    fun onCoolDown() = repo.coolDown(serverId)

    fun onUpdateVersion() = repo.updateVersion(serverId)

    fun onReset() = repo.reset(serverId)
}