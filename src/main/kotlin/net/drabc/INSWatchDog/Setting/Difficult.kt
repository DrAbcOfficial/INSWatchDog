package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Difficult(
    var enable: Boolean = false,
    var maxDifficult: Double = 1.0,
    var maxToScale: Int = 8,
    var minDifficult: Double = 0.1,
    var minToScale: Int = 1,
    var renameServer: Boolean = false,
    var failureDifficultTweak: Boolean = false,
    var failureDifficultReduce: Double = 0.5,
    var failureDifficultMessage: String = "Difficult reduce to {0}% cause failing"
)