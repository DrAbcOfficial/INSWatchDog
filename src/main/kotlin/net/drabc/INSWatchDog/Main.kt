package net.drabc.INSWatchDog

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.drabc.INSWatchDog.Vars.Var
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Runnable.*
import net.drabc.INSWatchDog.SaidCommand.Register
import net.drabc.INSWatchDog.Setting.SettingBase
import sun.awt.OSInfo

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking { start() }
        }

        private suspend fun start() = coroutineScope {
            //1
            try{
                Var.settingBase = Utility.parseJson("/config.json") as SettingBase
            }
            catch (e: Exception){
                Var.logger.log("配置文件读取错误!", Logger.LogType.SEVERE)
                Var.logger.exception(e)
                Var.logger.log("程序已放弃启动", Logger.LogType.SEVERE)
                return@coroutineScope
            }
            Var.logger.log("配置文件读取完毕！", Logger.LogType.WARN)
            Var.nowMaxDifficult = arrayOf(Var.settingBase.difficult.maxDifficult, Var.settingBase.difficult.minDifficult)
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