package net.drabc.inswatchdog.runnable

import net.drabc.inswatchdog.Logger
import net.drabc.inswatchdog.OS
import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.vars.Var
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.concurrent.thread

class HeartBeatWatcher: BaseRunnable(_forceExec = true) {
    override suspend fun execute(client: RconClient) {
        if(LogWatcher.gameStatue == LogWatcher.GameStatues.Dead)
            deathCount++

       if(deathCount >= Var.settingBase.heartBeat.maxAllowDeathCount){
           Var.logger.log(Var.settingBase.heartBeat.killGame, Logger.LogType.SEVERE)

           lateinit var reader: BufferedReader
           lateinit var process: Process
           try {
               thread(true) {
                   //杀掉进程
                   process = Runtime.getRuntime().exec(
                           when(Var.osType){
                               OS.WINDOWS -> "taskkill /IM InsurgencyServer.exe /F"
                               OS.LINUX -> "kill -9 \$(pidof InsurgencyServer-Linux-Shipping)"
                               else -> ""
                           }
                   )
                   reader = BufferedReader(InputStreamReader(process.inputStream, "utf-8"))
                   var line: String?
                   while (reader.readLine().also { line = it } != null) {
                       Var.logger.log("kill PID return info -----> $line")
                   }
               }
           } catch (e: Exception) { Var.logger.exception(e) }
           finally {
               process.destroy()
               try {
                   thread(true) { reader.close() }
               } catch (e: IOException) {Var.logger.exception(e)}
           }

           thread(true) {
               gameProcess = ProcessBuilder("${Var.settingBase.setting.rootDir}/${Var.settingBase.heartBeat.executableFile}").start()
           }
           deathCount=0
           cleanVar()
           LogWatcher.gameStatue = LogWatcher.GameStatues.Undefine
       }
    }

    private fun cleanVar() {
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
        Var.logger.log(Var.settingBase.heartBeat.localOs.replace("{0}", Var.osType.toString()))
    }
}