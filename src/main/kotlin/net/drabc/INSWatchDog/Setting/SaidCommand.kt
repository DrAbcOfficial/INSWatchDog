package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SaidCommand(
    var helpHelpMessage: String = "List all commands",
    var helpMaxLine: Int = 5,
    var helpMessageButtom: String = "help {0} change page | all {1} page",
    var helpMessageTop: String = "Available commands:"
)