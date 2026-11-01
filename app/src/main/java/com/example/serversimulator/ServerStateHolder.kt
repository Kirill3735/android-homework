package com.example.serversimulator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ServerStateHolder(
    private val initialState: ServerUiState = ServerUiState()
) {
    var uiState by mutableStateOf(initialState)
        private set

    fun rename(newName: String) {
        if (!uiState.canRename) return
        uiState = uiState.copy(name = newName)
    }

    fun start() {
        if (!uiState.canStart) return
        val temp = maxOf(uiState.temperature, ServerRules.START_TEMP)
        uiState = uiState.copy(
            status = statusFor(ServerRules.START_CPU, temp),
            cpuLoad = ServerRules.START_CPU,
            temperature = temp
        )
    }

    fun addLoad() {
        if (!uiState.canAddLoad) return
        applyMetrics(
            cpuDelta = ServerRules.LOAD_CPU_STEP,
            tempDelta = ServerRules.LOAD_TEMP_STEP
        )
    }

    fun coolDown() {
        if (!uiState.canCoolDown) return
        uiState = when (uiState.status) {
            ServerStatus.CRASHED -> uiState.copy(
                status = ServerStatus.OFFLINE,
                cpuLoad = ServerRules.OFFLINE_CPU,
                temperature = ServerRules.POST_CRASH_TEMP
            )
            ServerStatus.OFFLINE -> uiState.copy(
                temperature = (uiState.temperature - ServerRules.COOL_TEMP_STEP)
                    .coerceAtLeast(ServerRules.AMBIENT_TEMP)
            )
            ServerStatus.RUNNING, ServerStatus.OVERLOADED -> {
                val cpu = (uiState.cpuLoad - ServerRules.COOL_CPU_STEP)
                    .coerceAtLeast(ServerRules.MIN_RUNNING_CPU)
                val temp = (uiState.temperature - ServerRules.COOL_TEMP_STEP)
                    .coerceAtLeast(ServerRules.MIN_RUNNING_TEMP)
                uiState.copy(status = statusFor(cpu, temp), cpuLoad = cpu, temperature = temp)
            }
        }
    }

    fun updateVersion() {
        if (!uiState.canUpdateVersion) return
        applyMetrics(
            cpuDelta = ServerRules.UPDATE_CPU_STEP,
            tempDelta = ServerRules.UPDATE_TEMP_STEP,
            versionDelta = 1
        )
    }

    fun reset() {
        uiState = initialState
    }

    private fun applyMetrics(cpuDelta: Int, tempDelta: Int, versionDelta: Int = 0) {
        val cpu = (uiState.cpuLoad + cpuDelta).coerceAtMost(ServerRules.MAX_CPU)
        val temp = (uiState.temperature + tempDelta).coerceAtMost(ServerRules.MAX_TEMP)
        uiState = uiState.copy(
            status = statusFor(cpu, temp),
            cpuLoad = cpu,
            temperature = temp,
            version = uiState.version + versionDelta
        )
    }

    private fun statusFor(cpu: Int, temp: Int): ServerStatus = when {
        cpu >= ServerRules.CRASH_CPU_THRESHOLD ||
                temp >= ServerRules.CRASH_TEMP_THRESHOLD -> ServerStatus.CRASHED
        cpu >= ServerRules.OVERLOAD_CPU_THRESHOLD ||
                temp >= ServerRules.OVERLOAD_TEMP_THRESHOLD -> ServerStatus.OVERLOADED
        else -> ServerStatus.RUNNING
    }
}