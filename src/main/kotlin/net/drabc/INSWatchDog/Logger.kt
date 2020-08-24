package net.drabc.INSWatchDog

import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Logger
import java.util.logging.Level

class Logger {
    val globalLogger = Logger.getLogger("net.drabc.logger")
    val logPath = File(Utility.getUserDir() +
            "/Log/${DateTimeFormatter.ofPattern("YYYY-MM-dd").format(
        LocalDateTime.now())}/" + UUID.randomUUID() + ".log")
    init {
        globalLogger.level = Level.ALL
    }

    enum class LogType{
        FINE, INFO, WARN, SEVERE
    }

    private fun SnapLog(message: String, type: LogType): String{
        var tempString = ""
        tempString += when(type){
            LogType.FINE -> "FINE"
            LogType.INFO -> "INFO"
            LogType.WARN -> "WARN"
            LogType.SEVERE -> "SEVERE"
        }
        tempString += " [${LocalDateTime.now().toString()}] -> " + message
        return tempString
    }
    fun Log(message: String, type: LogType = LogType.INFO){
        when(type){
            LogType.FINE -> globalLogger.fine(message)
            LogType.INFO -> globalLogger.info(message)
            LogType.WARN -> globalLogger.warning(message)
            LogType.SEVERE -> globalLogger.severe(message)
        }
        if (!logPath.parentFile.exists())
            logPath.parentFile.mkdirs()
        if(!logPath.exists())
            logPath.createNewFile()
        val fw = FileWriter(logPath, true)
        fw.write(SnapLog(message, type) + "\n")
        fw.close()
    }
}