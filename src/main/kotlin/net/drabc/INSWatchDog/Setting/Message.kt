package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    var format: Format = Format(
        "AI tweak to {0}%",
        "{0}th player {1} joined game",
        "{0} points",
        "{0}[{1}] left game",
        "No one",
        "Program stated",
        "[MM dd HH:mm]"
    ),
    var msgHeader: String = "Watchdog",
    var msgList: List<String> = listOf("Welcome to Ins server", "Please enjoy game here"),
    var waitTime: Int = 40
)