package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatueMessage(
    var GameOver: String,
    var PostRound: String,
    var PreRound: String,
    var RoundActive: String,
    var RoundFailed: String,
    var RoundWon: String
)