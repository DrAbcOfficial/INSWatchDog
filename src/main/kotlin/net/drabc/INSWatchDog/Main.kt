package net.drabc.inswatchdog

import com.squareup.moshi.Moshi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.drabc.inswatchdog.vars.Var
import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.runnable.*
import net.drabc.inswatchdog.saidcommand.Register
import net.drabc.inswatchdog.setting.SettingBase
import sun.awt.OSInfo
import java.io.FileWriter
import java.nio.file.Paths
import kotlin.system.exitProcess

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking { start() }
        }

        private suspend fun start() = coroutineScope {
            //1
            val configFile = Paths.get( "${Utility.getUserDir()}/config.json").toFile()
            try{

                if(configFile.exists())
                    Var.settingBase = Utility.parseJson(configFile.path) as SettingBase
                else{
                    Var.logger.log("配置文件不存在, 将于${configFile.path}生成默认配置\n请修改后重启程序!", Logger.LogType.SEVERE)
                    configFile.createNewFile()
                    FileWriter(configFile, false).use{
                        it.write(Moshi.Builder().build().adapter(SettingBase::class.java).toJson(Utility.getEmptySettingBase()))
                    }
                    Utility.pressKeytoContinue()
                    exitProcess(-1)
                }
            }
            catch (e: Exception){
                Var.logger.log("配置文件读取错误!", Logger.LogType.SEVERE)
                Var.logger.exception(e)
                Var.logger.log("程序已放弃启动", Logger.LogType.SEVERE)
                Utility.pressKeytoContinue()
                exitProcess(-1)
            }
            Var.logger.log("配置文件读取完毕！", Logger.LogType.WARN)
            Var.defaultSettingBase = Var.settingBase
            Var.logger.log("已设置log路径${Var.logger.logPath.path}")
            //2
            Register.registerSaidCommand()
            //3
            try {
                val rconClient = RconClient()
                rconClient.connect(
                    Var.settingBase.rcon.ipAddress,
                    Var.settingBase.rcon.rconPort,
                    Var.settingBase.rcon.passWd
                )
                Utility.getMapList(rconClient)
                launch {
                    LogWatcher().run(rconClient)
                }
                launch {
                    Message().run(rconClient)
                }
                launch {
                    SyncPlayerList().run(rconClient)
                }
                if (Var.settingBase.difficult.enable) {
                    launch {
                        DifficultTweak().run(rconClient)
                    }
                }
                if (Var.settingBase.soloBot.enable) {
                    launch {
                        SoloBotTweak().run(rconClient)
                    }
                }
                if(Var.settingBase.heartBeat.enable){
                if (Var.systemType == OSInfo.OSType.LINUX || Var.systemType == OSInfo.OSType.WINDOWS) {
                    launch {
                        HeartBeatWatcher().run(rconClient)
                    }
                }
                else{
                    Var.logger.log(Var.settingBase.heartBeat.unkownOs.replace("{0}", Var.systemType.name))
                }}
            }catch (e: Exception){
                Var.logger.exception(e)
            }
        }
    }
}