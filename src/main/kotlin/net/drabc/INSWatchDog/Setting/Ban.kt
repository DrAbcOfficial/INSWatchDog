package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ban(
    var banPlayers: List<BanPlayer> = listOf(BanPlayer(71234567890, "I wanna kick ur ass")),
    var kickByName: String = "Bad guy",
    var kickKeyWordFile: String = "KickKeyword"
)