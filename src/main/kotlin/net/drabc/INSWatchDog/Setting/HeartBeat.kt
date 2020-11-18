package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HeartBeat(
    var enable: Boolean = false,
    var excutableFile: String = "sandstorm.sh",
    var maxAllowDeathCount: Int = 3,
    var unkownOs: String = "Unknown OS {0}, abandon executing",
    var localOs: String = "Local OS is {0}",
    var killGame: String = "Game process dead, killing..."
)