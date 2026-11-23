package com.example.serversimulator

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.roundToInt

private const val LOG_TAG = "ServerRepository"

class ServerRepository(private val api: WeatherApi) {

    var servers by mutableStateOf(startServers())
        private set

    private var nextId = servers.maxOf { it.id } + 1

    fun server(id: Int): Server? = servers.find { it.id == id }

    // Новый сервер в исходном состоянии появляется в начале списка
    fun add() {
        servers = listOf(Server(id = nextId++)) + servers
    }

    fun remove(id: Int) {
        servers = servers.filterNot { it.id == id }
    }

    fun rename(id: Int, name: String) = update(id) { it.rename(name) }

    fun start(id: Int) = update(id) { it.start() }

    fun addLoad(id: Int) = update(id) { it.addLoad() }

    fun coolDown(id: Int, outsideTemp: Int?) = update(id) { it.coolDown(outsideTemp) }

    fun updateVersion(id: Int) = update(id) { it.updateVersion() }

    fun reset(id: Int) = update(id) { it.reset() }

    // Температура воздуха у дата-центра, °C.
    suspend fun outsideTemperature(dataCenter: DataCenter): Int = try {
        retryIO { api.currentWeather(dataCenter.latitude, dataCenter.longitude) }
            .current.temperature.roundToInt()
    } catch (e: IOException) {
        throw WeatherLoadException(canRetry = true, e)
    } catch (e: HttpException) {
        throw WeatherLoadException(canRetry = e.code() >= NetworkConfig.HTTP_SERVER_ERROR, e)
    } catch (e: SerializationException) {
        Log.e(LOG_TAG, "Ответ сервиса погоды не совпал с DTO", e)
        throw WeatherLoadException(canRetry = false, e)
    }

    // Меняется только сервер с этим id
    private fun update(id: Int, transform: (Server) -> Server) {
        servers = servers.map { if (it.id == id) transform(it) else it }
    }
}

// Стартовые серверы в разных состояниях
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