package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncPlayerList(
    var waitTime: Int = 1
)