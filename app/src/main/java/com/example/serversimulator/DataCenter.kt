package com.example.serversimulator

//Где стоит сервер. Координаты нужны, чтобы узнать погоду у дата-центра
enum class DataCenter(val city: String, val latitude: Double, val longitude: Double) {
    VLADIVOSTOK("Владивосток", 43.12, 131.89),
    MOSCOW("Москва", 55.75, 37.62),
    NOVOSIBIRSK("Новосибирск", 55.03, 82.92),
    MURMANSK("Мурманск", 68.97, 33.07),
    SOCHI("Сочи", 43.60, 39.73);

    companion object {
        fun forId(id: Int): DataCenter = DataCenter.entries[id % DataCenter.entries.size]
    }
}