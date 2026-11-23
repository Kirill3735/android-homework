package com.example.serversimulator

import androidx.lifecycle.ViewModel

data class ServerListUiState(
    val servers: List<Server> = emptyList()
)

class ServerListViewModel(private val repo: ServerRepository) : ViewModel() {

    val uiState: ServerListUiState
        get() = ServerListUiState(servers = repo.servers)

    fun onAdd() = repo.add()

    fun onDelete(id: Int) = repo.remove(id)
}