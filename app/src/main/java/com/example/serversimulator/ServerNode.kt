package com.example.serversimulator

enum class ServerStatus(val label: String) {
    OFFLINE("Остановлен"),
    RUNNING("Работает"),
    OVERLOADED("Перегрузка"),
    CRASHED("Авария (Сбой)")
}

data class ServerNode(
    val name: String = "Backend-01",
    val status: ServerStatus = ServerStatus.OFFLINE,
    val cpuLoad: Int = 0,
    val temperature: Int = 30,
    val version: Int = 1
) {
    val canStart: Boolean
        get() = status == ServerStatus.OFFLINE && temperature < 70

    val canAddLoad: Boolean
        get() = status == ServerStatus.RUNNING || status == ServerStatus.OVERLOADED

    val canCoolDown: Boolean
        get() = status != ServerStatus.OFFLINE && (cpuLoad > 0 || temperature > 30)

    val canUpdateVersion: Boolean
        get() = status == ServerStatus.RUNNING && cpuLoad <= 60 && temperature <= 65

    fun start(): ServerNode {
        if (!canStart) return this
        return copy(
            status = ServerStatus.RUNNING,
            cpuLoad = 20,
            temperature = 40
        )
    }

    fun addLoad(): ServerNode {
        if (!canAddLoad) return this
        val newCpu = (cpuLoad + 30).coerceAtMost(100)
        val newTemp = (temperature + 20).coerceAtMost(100)

        val newStatus = when {
            newCpu >= 100 || newTemp >= 90 -> ServerStatus.CRASHED
            newCpu >= 70 || newTemp >= 70 -> ServerStatus.OVERLOADED
            else -> ServerStatus.RUNNING
        }

        return copy(
            status = newStatus,
            cpuLoad = newCpu,
            temperature = newTemp
        )
    }

    fun coolDown(): ServerNode {
        if (!canCoolDown) return this
        if (status == ServerStatus.CRASHED) {
            return copy(status = ServerStatus.OFFLINE, cpuLoad = 0, temperature = 45)
        }
        val newCpu = (cpuLoad - 30).coerceAtLeast(10)
        val newTemp = (temperature - 20).coerceAtLeast(35)
        val newStatus = if (newCpu >= 70 || newTemp >= 70) {
            ServerStatus.OVERLOADED
        } else {
            ServerStatus.RUNNING
        }
        return copy(status = newStatus, cpuLoad = newCpu, temperature = newTemp)
    }

    fun updateVersion(): ServerNode {
        if (!canUpdateVersion) return this
        return copy(
            version = version + 1,
            cpuLoad = cpuLoad + 15,
            temperature = temperature + 10
        )
    }
}