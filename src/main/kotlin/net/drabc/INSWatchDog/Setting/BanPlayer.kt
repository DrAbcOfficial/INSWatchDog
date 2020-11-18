package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BanPlayer(
    var id: Long,
    var reason: String
)