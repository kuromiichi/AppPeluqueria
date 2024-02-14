package dev.kuromiichi.apppeluqueria.models

data class Appointment(
    val userName: String,
    val userUid: String,
    val date: String,
    val time: String,
    val duration: Int,
    val services: List<Service>
    )