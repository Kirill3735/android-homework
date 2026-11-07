package com.example.serversimulator

enum class ServerStatus(val label: String) {
    OFFLINE("Остановлен"),
    RUNNING("Работает"),
    OVERLOADED("Перегрузка"),
    CRASHED("Авария (Сбой)")
}

data class Server(
    val id: Int,
    val name: String = ServerRules.defaultName(id),
    val status: ServerStatus = ServerStatus.OFFLINE,
    val cpuLoad: Int = ServerRules.OFFLINE_CPU,
    val temperature: Int = ServerRules.AMBIENT_TEMP,
    val version: Int = ServerRules.INITIAL_VERSION
) {
    val canRename: Boolean
        get() = status != ServerStatus.CRASHED

   // После аварии сервер остаётся горячим и не запускается, пока не остынет.
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

    fun rename(newName: String): Server =
        if (canRename) copy(name = newName) else this

    fun start(): Server {
        if (!canStart) return this
        // Горячий сервер не охлаждается сам при запуске
        return copy(
            status = ServerStatus.RUNNING,
            cpuLoad = ServerRules.START_CPU,
            temperature = maxOf(temperature, ServerRules.START_TEMP)
        )
    }

    fun addLoad(): Server {
        if (!canAddLoad) return this
        return withLoad(
            cpu = (cpuLoad + ServerRules.LOAD_CPU_STEP).coerceAtMost(ServerRules.MAX_CPU),
            temp = (temperature + ServerRules.LOAD_TEMP_STEP).coerceAtMost(ServerRules.MAX_TEMP)
        )
    }

    fun coolDown(): Server {
        if (!canCoolDown) return this
        return when (status) {
            ServerStatus.CRASHED -> copy(
                status = ServerStatus.OFFLINE,
                cpuLoad = ServerRules.OFFLINE_CPU,
                temperature = ServerRules.POST_CRASH_TEMP
            )
            ServerStatus.OFFLINE -> copy(
                temperature = (temperature - ServerRules.COOL_TEMP_STEP)
                    .coerceAtLeast(ServerRules.AMBIENT_TEMP)
            )
            ServerStatus.RUNNING, ServerStatus.OVERLOADED -> withLoad(
                cpu = (cpuLoad - ServerRules.COOL_CPU_STEP)
                    .coerceAtLeast(ServerRules.MIN_RUNNING_CPU),
                temp = (temperature - ServerRules.COOL_TEMP_STEP)
                    .coerceAtLeast(ServerRules.MIN_RUNNING_TEMP)
            )
        }
    }

    fun updateVersion(): Server {
        if (!canUpdateVersion) return this
        return withLoad(
            cpu = cpuLoad + ServerRules.UPDATE_CPU_STEP,
            temp = temperature + ServerRules.UPDATE_TEMP_STEP
        ).copy(version = version + 1)
    }

    fun reset(): Server = Server(id = id, name = name)

    private fun withLoad(cpu: Int, temp: Int): Server =
        copy(status = statusFor(cpu, temp), cpuLoad = cpu, temperature = temp)

    private fun statusFor(cpu: Int, temp: Int): ServerStatus = when {
        cpu >= ServerRules.CRASH_CPU_THRESHOLD ||
                temp >= ServerRules.CRASH_TEMP_THRESHOLD -> ServerStatus.CRASHED
        cpu >= ServerRules.OVERLOAD_CPU_THRESHOLD ||
                temp >= ServerRules.OVERLOAD_TEMP_THRESHOLD -> ServerStatus.OVERLOADED
        else -> ServerStatus.RUNNING
    }
}