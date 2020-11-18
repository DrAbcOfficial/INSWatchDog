package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SettingBase(
    var CVars: CVars,
    var ban: Ban,
    var difficult: Difficult,
    var heartBeat: HeartBeat,
    var logWatcher: LogWatcher,
    var message: Message,
    var rcon: Rcon,
    var saidCommand: SaidCommand,
    var setting: Setting,
    var soloBot: SoloBot,
    var syncPlayerList: SyncPlayerList
)