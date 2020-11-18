package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogWatcher(
    var statueMessage: StatueMessage = StatueMessage(
        "Game Over", "Round Over, best player is {bestplayer}", "Ready for mission",
        "Go go go", "Nice try", "GGWP"
    ),
    var waitTime: Int = 1
)