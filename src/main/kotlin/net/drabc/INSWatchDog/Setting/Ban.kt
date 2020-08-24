package net.drabc.INSWatchDog.Setting

data class Ban(
    val banPlayers: List<BanPlayer>,
    val kickByName: String,
    val kickKeyWordFile: String
)