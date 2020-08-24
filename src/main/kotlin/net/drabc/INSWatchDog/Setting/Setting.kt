package net.drabc.INSWatchDog.Setting

data class Setting(
    val defaultCvarSets: String,
    val waitTime: Int,
    val hideEmpty: Boolean,
    val serverName: String,
    val rootDir: String,
    val excutableFile: String
)