package dev.kuromiichi.apppeluqueria.listeners

import dev.kuromiichi.apppeluqueria.models.Service

interface ServiceOnClickListener {
    fun onServiceClick(service: Service)
}