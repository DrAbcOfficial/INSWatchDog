package net.drabc.INSWatchDog

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.drabc.INSWatchDog.Vars.Var
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Setting.SettingBase

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking { strat() }
        }

        private suspend fun strat() = coroutineScope {
            Var.settingBase = Utility.parseJson("/config.json") as SettingBase
            Var.logger.Log("配置文件读取完毕！", Logger.LogType.WARN);
            val rconClient = RconClient()
            rconClient.connect(Var.settingBase.rcon.ipAddress, Var.settingBase.rcon.rconPort, Var.settingBase.rcon.passWd)
            Utility.getMapList(rconClient)
            Var.logger.Log("启动message协程", Logger.LogType.WARN)
            launch {
                Message.run(rconClient)
            }
            Var.logger.Log("启动syncplayers协程", Logger.LogType.WARN)
            launch {
                SyncPlayerList.run(rconClient)
            }
            if(Var.settingBase.difficult.enable){
                Var.logger.Log("启动difficultTweak协程", Logger.LogType.WARN)
                launch {
                    DifficultTweak.run(rconClient)
                }
            }
            if(Var.settingBase.soloBot.enable){
                Var.logger.Log("启动soloBotTweak协程", Logger.LogType.WARN)
                launch {
                    SoloBotTweak.run(rconClient)
                }
            }
        }
    }
}