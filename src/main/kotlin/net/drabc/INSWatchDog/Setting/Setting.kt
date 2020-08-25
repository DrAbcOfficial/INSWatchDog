package net.drabc.INSWatchDog.Setting

data class Setting(
    val defaultCvarSets: String,
    val excutableFile: String,
    val hideEmpty: Boolean,
    val rootDir: String,
    val serverName: String,
    val waitTime: Int
)