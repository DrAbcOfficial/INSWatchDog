@file:Suppress("BlockingMethodInNonBlockingContext")

package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.SaidCommand.Register
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Var
import net.drabc.INSWatchDog.Vars.getPlayer
import java.io.File
import java.io.FileInputStream

class LogWatcher: BaseRunnable(Var.settingBase.logWatcher.waitTime, true) {
    override suspend fun execute(client: RconClient) = if(logFile.exists() && logFile.canRead()){
        FileInputStream(logFile).run{
            this.skip(filePointer)
            filePointer = logFile.length()
            this.reader().readLines().forEach {
                //查看状态
                //无视前30个无用字符
                if(it.length > 30) {
                    val tempString = it.substring(30)
                    when {
                        tempString.contains("LogGameMode: Display: State:") -> {
                            val gameState = tempString.split("->")[1].trim()
                            gameStatue = when (gameState) {
                                "GameOver" -> GameStatues.GameOver
                                "LeavingMap" -> GameStatues.LeavingMap
                                "EnteringMap" -> GameStatues.EnteringMap
                                "LoadingAssets" -> GameStatues.LoadingAssets
                                "WaitingToStart" -> GameStatues.WaitingToStart
                                "GameStarting" -> GameStatues.GameStarting
                                "PreRound" -> GameStatues.PreRound
                                "RoundActive" -> GameStatues.RoundActive
                                "RoundWon" -> GameStatues.RoundWon
                                "PostRound" -> GameStatues.PostRound
                                "WaitingPostMatch" -> GameStatues.WaitingPostMatch
                                else -> GameStatues.Undefine
                            }
                            when (gameStatue) {
                                GameStatues.GameOver -> {
                                    Utility.sendMessage(client, Var.settingBase.logWatcher.statueMessage.GameOver)
                                    Var.playerList = mutableListOf()
                                }
                                GameStatues.PostRound -> Utility.sendMessage(
                                    client,
                                    Var.settingBase.logWatcher.statueMessage.PostRound
                                )
                                GameStatues.RoundActive -> Utility.sendMessage(
                                    client,
                                    Var.settingBase.logWatcher.statueMessage.RoundActive
                                )
                                GameStatues.PreRound -> Utility.sendMessage(
                                    client,
                                    Var.settingBase.logWatcher.statueMessage.PreRound
                                )
                                else -> {
                                }//Nothing
                            }
                        }
                        tempString.contains("LogGameMode: Display: Round Over") -> {
                            val wonReason = tempString.substring(58).trim(')')
                            Var.logger.log("回合结束，游戏${if (wonReason == "Objective") "胜利" else "失败"}")
                            Utility.sendMessage(
                                client,
                                if (wonReason == "Objective")
                                    Var.settingBase.logWatcher.statueMessage.RoundWon
                                else
                                    Var.settingBase.logWatcher.statueMessage.RoundFailed
                            )
                        }
                        tempString.contains("LogChat: Display: ") && tempString.contains(") Global Chat: ") -> {
                            val tempSaid = tempString.substring(18).split(") Global Chat: ")
                            var playerSaid = ""
                            if (tempSaid.size > 2)
                                for (i in 1..tempSaid.size)
                                    playerSaid += tempSaid[i]
                            else
                                playerSaid = tempSaid[1]
                            val playerID = tempSaid[0].takeLast(17).toLong()
                            if (playerSaid.startsWith('!'))
                                Register.execSaidCommand(client, playerSaid, Var.playerList.getPlayer(playerID))
                        }
                    }
                }
            }
            close()
        }
    }
    else
        Var.logger.log("log文件于 ${logFile.path} 不存在, 将等待下一次检查")

    enum class GameStatues{
        Dead,
        GameOver,
        LeavingMap,
        EnteringMap,
        LoadingAssets,
        WaitingToStart,
        GameStarting,
        PreRound,
        RoundActive,
        RoundWon,
        PostRound,
        WaitingPostMatch,
        Undefine
    }
    companion object{
        var gameStatue = GameStatues.Undefine
        private val logFile = File(Var.settingBase.setting.rootDir + "/Insurgency/Saved/Logs/Insurgency.log")
        private var filePointer : Long = 0
    }
    init {
        if(logFile.exists())
            filePointer = logFile.length()
    }
}
