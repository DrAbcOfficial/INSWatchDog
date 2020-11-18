package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rcon(
    var ipAddress: String = "127.0.0.1",
    var passWd: String = "secret",
    var rconPort: Int = 12345
)