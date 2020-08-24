package net.drabc.INSWatchDog.Setting

data class SoloBot(
    val enable: Boolean,
    val fullSetPlayer: Int,
    val maxBots: Int,
    val minBots: Int,
    val soloRegain: Int,
    val soloWaves: Int
)