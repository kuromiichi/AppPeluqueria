package dev.kuromiichi.apppeluqueria.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Service(
    val id: String = "",
    val name: String = "",
    val duration: Int = 0
) : Parcelable