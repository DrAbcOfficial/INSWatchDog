package net.drabc.INSWatchDog.Setting

data class SettingBase(
    val CVars: CVars,
    val ban: Ban,
    val difficult: Difficult,
    val logWatcher: LogWatcher,
    val message: Message,
    val rcon: Rcon,
    val setting: Setting,
    val soloBot: SoloBot,
    val syncPlayerList: SyncPlayerList
)