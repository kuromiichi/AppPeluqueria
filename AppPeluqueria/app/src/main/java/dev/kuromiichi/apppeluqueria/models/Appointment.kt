package dev.kuromiichi.apppeluqueria.models

import java.util.Date

data class Appointment(
    val id: String = "",
    val userUid: String = "",
    val date: Date = Date(),
    val time: Date = Date(),
    val services: List<Service> = emptyList()
) {
    val duration: Int get() = services.sumOf { it.duration }
}