package net.drabc.INSWatchDog

import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class Logger {
    private val globalLogger: Logger = Logger.getLogger("net.drabc.logger")!!
    val logPath = File(
        Utility.getUserDir() +
                "/Log/${
                    DateTimeFormatter.ofPattern("YYYY-MM-dd").format(
                        LocalDateTime.now()
                    )
                }/" + UUID.randomUUID() + ".log"
    )
    init {
        globalLogger.level = Level.ALL
        /**
        if (!logPath.parentFile.exists())
            logPath.parentFile.mkdirs()
        if(!logPath.exists())
            logPath.createNewFile()
        val fileHandler = FileHandler(logPath.path)
        fileHandler.formatter = SimpleFormatter()
        globalLogger.addHandler(fileHandler)
        **/
    }

    enum class LogType{
        FINEST, FINE, INFO, WARN, SEVERE
    }

    private fun snapLog(message: String, type: LogType): String{
        var tempString = ""
        tempString += when(type){
            LogType.FINEST -> "FINEST"
            LogType.FINE -> "FINE"
            LogType.INFO -> "INFO"
            LogType.WARN -> "WARN"
            LogType.SEVERE -> "SEVERE"
        }
        tempString += " [${LocalDateTime.now()}] -> " + message
        return tempString
    }
    fun log(message: String, type: LogType = LogType.INFO){
        when(type){
            LogType.FINEST -> globalLogger.finest(message)
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
        fw.write(snapLog(message, type) + "\n")
        fw.close()
    }
    fun exception(e: Exception){
        var tempString = "Cautch a exception->\n\tMessage: ${e.localizedMessage}\n" +
                "\tCause ${e.cause.toString()}\n" +
                "\tJavaClass: ${e.javaClass}\n\tSuppressed: \n"
        e.suppressed.forEach {
            tempString += "\t\t$it"
        }
        tempString += "\n\tStackTrace: \n"
        e.stackTrace.forEach {
            tempString += "\t\t$it\n"
        }
        this.log(tempString, LogType.SEVERE)
    }
}