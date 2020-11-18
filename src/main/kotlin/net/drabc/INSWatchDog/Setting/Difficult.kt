package net.drabc.INSWatchDog.Setting

data class Difficult(
    val enable: Boolean,
    val maxDifficult: Double,
    val maxToScale: Int,
    val minDifficult: Double,
    val minToScale: Int,
    val renameServer: Boolean,
    val failureDifficultTweak: Boolean,
    val failureDifficultReduce: Double,
    val failureDifficultMessage: String
)