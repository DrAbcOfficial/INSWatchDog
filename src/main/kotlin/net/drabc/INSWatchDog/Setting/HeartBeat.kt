package net.drabc.INSWatchDog.Setting

data class HeartBeat(
    val enable: Boolean,
    val excutableFile: String,
    val maxAllowDeathCount: Int,
    val unkownOs: String,
    val localOs: String,
    val killGame: String
)