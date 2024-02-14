package dev.kuromiichi.apppeluqueria.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Service(
    val name: String = "",
    val time: String = ""
) : Parcelable