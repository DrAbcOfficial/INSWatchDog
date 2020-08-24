package net.drabc.INSWatchDog.Setting

data class Message(
    val format: Format,
    val msgHeader: String,
    val msgList: List<String>,
    val waitTime: Int
)