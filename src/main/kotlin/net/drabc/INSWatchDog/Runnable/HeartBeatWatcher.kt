package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.Logger
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Vars.Var
import sun.awt.OSInfo.OSType
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class HeartBeatWatcher: BaseRunnable() {
    override suspend fun execute(client: RconClient) {
        if(LogWatcher.gameStatue == LogWatcher.GameStatues.Dead)
            deathCount++

       if(deathCount >= Var.settingBase.heartBeat.maxAllowDeathCount){
           Var.logger.log(Var.settingBase.heartBeat.killGame, Logger.LogType.SEVERE)

           var reader: BufferedReader? = null
           var process: Process? = null
           try {
               //杀掉进程
               process = Runtime.getRuntime().exec(
                   when(Var.systemType){
                       OSType.WINDOWS -> "taskkill /IM InsurgencyServer.exe /F"
                       OSType.LINUX -> "kill -9 \$(pidof InsurgencyServer-Linux-Shipping)"
                       else -> ""
                   }
               )
               reader = BufferedReader(InputStreamReader(process.inputStream, "utf-8"))
               var line: String?
               while (reader.readLine().also { line = it } != null) {
                   Var.logger.log("kill PID return info -----> $line")
               }
           } catch (e: Exception) { Var.logger.exception(e) }
           finally {
               process?.destroy()
               try {
                   reader?.close()
               } catch (e: IOException) {Var.logger.exception(e)}
           }

           gameProcess = ProcessBuilder("${Var.settingBase.setting.rootDir}/${Var.settingBase.heartBeat.excutableFile}").start()
           deathCount=0
           cleanVar()
           LogWatcher.gameStatue = LogWatcher.GameStatues.Undefine
       }
    }

    fun cleanVar() {
        Var.nowDifficult = 0.0
        Var.playerList = mutableListOf()
        Var.mapsName = mutableListOf()
        Var.nowBotCount = 0
    }

    companion object{
        var deathCount: Int = 0
        lateinit var gameProcess: Process
    }
    init {
        Var.logger.log(Var.settingBase.heartBeat.localOs.replace("{0}", Var.systemType.name))
    }
}