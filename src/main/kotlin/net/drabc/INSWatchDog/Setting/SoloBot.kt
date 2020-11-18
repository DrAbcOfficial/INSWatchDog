package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SoloBot(
    var enable: Boolean = false,
    var fullSetPlayer: Int = 8,
    var maxBots: Int = 100,
    var minBots: Int = 1,
    var soloRegain: Int = 50,
    var soloWaves: Int = 666
)