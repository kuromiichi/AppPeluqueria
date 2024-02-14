package dev.kuromiichi.apppeluqueria.listeners

import dev.kuromiichi.apppeluqueria.models.Appointment

interface AppointmentOnClickListener {

    fun onAppointmentClick(appointment: Appointment)
}