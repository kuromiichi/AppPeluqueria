package dev.kuromiichi.apppeluqueria.models

data class Appointment(
    val id: String = "",
    val userUid: String = "",
    val date: String = "",
    val time: String = "",
    val services: List<Service> = emptyList()
) {
    val duration: Int get() = services.sumOf { it.duration }
}