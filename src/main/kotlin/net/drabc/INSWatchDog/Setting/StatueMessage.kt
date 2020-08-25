package net.drabc.INSWatchDog.Setting

data class StatueMessage(
    val GameOver: String,
    val PostRound: String,
    val PreRound: String,
    val RoundActive: String,
    val RoundFailed: String,
    val RoundWon: String
)