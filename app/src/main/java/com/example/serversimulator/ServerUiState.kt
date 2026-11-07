package com.example.serversimulator

enum class ServerStatus(val label: String) {
    OFFLINE("Остановлен"),
    RUNNING("Работает"),
    OVERLOADED("Перегрузка"),
    CRASHED("Авария (Сбой)")
}

data class ServerUiState(
    val name: String = ServerRules.DEFAULT_NAME,
    val status: ServerStatus = ServerStatus.OFFLINE,
    val cpuLoad: Int = ServerRules.OFFLINE_CPU,
    val temperature: Int = ServerRules.AMBIENT_TEMP,
    val version: Int = ServerRules.INITIAL_VERSION
) {
    val canRename: Boolean
        get() = status != ServerStatus.CRASHED

    val canStart: Boolean
        get() = status == ServerStatus.OFFLINE &&
                temperature < ServerRules.MAX_START_TEMP

    val isTooHotToStart: Boolean
        get() = status == ServerStatus.OFFLINE &&
                temperature >= ServerRules.MAX_START_TEMP

    val canAddLoad: Boolean
        get() = status == ServerStatus.RUNNING || status == ServerStatus.OVERLOADED

    val canCoolDown: Boolean
        get() = when (status) {
            ServerStatus.OFFLINE -> temperature > ServerRules.AMBIENT_TEMP
            ServerStatus.RUNNING, ServerStatus.OVERLOADED ->
                cpuLoad > ServerRules.MIN_RUNNING_CPU ||
                        temperature > ServerRules.MIN_RUNNING_TEMP
            ServerStatus.CRASHED -> true
        }

    val canUpdateVersion: Boolean
        get() = status == ServerStatus.RUNNING &&
                cpuLoad <= ServerRules.MAX_UPDATE_CPU &&
                temperature <= ServerRules.MAX_UPDATE_TEMP
}