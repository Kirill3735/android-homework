package com.example.serversimulator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

//Температура снаружи у дата-центра: ровно одно из трёх состояний
sealed interface WeatherUiState {
    data object Loading : WeatherUiState

    data class Content(val temperature: Int) : WeatherUiState {
        val isFreeCooling: Boolean
            get() = ServerRules.isFreeCooling(temperature)
    }

    data class Error(val canRetry: Boolean) : WeatherUiState
}

data class ServerUiState(
    val server: Server?,
    val weather: WeatherUiState = WeatherUiState.Loading
)

class ServerViewModel(
    private val serverId: Int,
    private val repo: ServerRepository
) : ViewModel() {

    private var weather: WeatherUiState by mutableStateOf(WeatherUiState.Loading)

    private var loadJob: Job? = null

    val uiState: ServerUiState
        get() = ServerUiState(server = repo.server(serverId), weather = weather)

    init {
        loadWeather()
    }

    fun onRename(name: String) = repo.rename(serverId, name)

    fun onStart() = repo.start(serverId)

    fun onAddLoad() = repo.addLoad(serverId)

    fun onCoolDown() = repo.coolDown(serverId, (weather as? WeatherUiState.Content)?.temperature)

    fun onUpdateVersion() = repo.updateVersion(serverId)

    fun onReset() = repo.reset(serverId)

    fun onRetryWeather() = loadWeather()

    private fun loadWeather() {
        if (loadJob?.isActive == true) return
        val dataCenter = repo.server(serverId)?.dataCenter ?: return
        loadJob = viewModelScope.launch {
            weather = WeatherUiState.Loading
            weather = fetchWeather(dataCenter)
        }
    }

    private suspend fun fetchWeather(dataCenter: DataCenter): WeatherUiState = try {
        WeatherUiState.Content(repo.outsideTemperature(dataCenter))
    } catch (e: WeatherLoadException) {
        WeatherUiState.Error(e.canRetry)
    }
}