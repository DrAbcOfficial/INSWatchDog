package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Format(
    var aiDifficult: String,
    var join: String,
    var lastBest: String,
    var leave: String,
    var noBest: String,
    var started: String,
    var time: String
)