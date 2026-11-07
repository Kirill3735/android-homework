package com.example.serversimulator


object ServerRules {
    const val DEFAULT_NAME = "Backend-01"
    const val INITIAL_VERSION = 1

    // Температуры и нагрузка в исходном и остановленном состоянии
    const val AMBIENT_TEMP = 30
    const val OFFLINE_CPU = 0
    const val POST_CRASH_TEMP = 75

    // Запуск
    const val MAX_START_TEMP = 70
    const val START_CPU = 20
    const val START_TEMP = 40

    // Минимумы работающего сервера
    const val MIN_RUNNING_CPU = 10
    const val MIN_RUNNING_TEMP = 35

    // Максимумы
    const val MAX_CPU = 100
    const val MAX_TEMP = 100

    // Шаги действий
    const val LOAD_CPU_STEP = 30
    const val LOAD_TEMP_STEP = 20
    const val COOL_CPU_STEP = 30
    const val COOL_TEMP_STEP = 20
    const val UPDATE_CPU_STEP = 15
    const val UPDATE_TEMP_STEP = 10

    // Условия обновления ПО
    const val MAX_UPDATE_CPU = 60
    const val MAX_UPDATE_TEMP = 65

    // Пороги состояний
    const val OVERLOAD_CPU_THRESHOLD = 70
    const val OVERLOAD_TEMP_THRESHOLD = 70
    const val CRASH_CPU_THRESHOLD = 100
    const val CRASH_TEMP_THRESHOLD = 90
}