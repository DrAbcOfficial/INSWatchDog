package net.drabc.INSWatchDog.Setting

data class Setting(
    val defaultCvarSets: String,
    val rtvMapList: List<String>,
    val hideEmpty: Boolean,
    val rootDir: String,
    val serverName: String,
    val waitTime: Int,
    val forceExec : Boolean
)