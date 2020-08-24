package net.drabc.INSWatchDog.Setting

data class Difficult(
    val enable: Boolean,
    val maxDifficult: Double,
    val maxToScale: Int,
    val minDifficult: Double,
    val minToScale: Int
)