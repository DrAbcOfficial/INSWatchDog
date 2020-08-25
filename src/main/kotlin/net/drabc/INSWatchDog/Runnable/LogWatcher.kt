package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Var
import java.io.File
import java.io.FileInputStream

class LogWatcher: BaseRunnable(Var.settingBase.logWatcher.waitTime, true) {
    override fun execute(client: RconClient) = if(logFile.exists() && logFile.canRead()){
        val fileStream = FileInputStream(logFile)
        fileStream.skip(filePointer)
        filePointer = logFile.length()
        fileStream.reader().readLines().forEach {
            //查看状态
            //无视前30个无用字符
            val tempString = it.substring(31)
            if(tempString.contains("LogGameMode: Display: State:")){
                val gameState = tempString.split("->")[1].trim()
                gameStatue = when(gameState){
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
                when(gameStatue){
                    GameStatues.GameOver -> Var.playerList = mutableListOf()
                    GameStatues.PostRound -> Utility.sendMessage(client, Var.settingBase.logWatcher.statueMessage.PostRound)
                    GameStatues.RoundActive -> Utility.sendMessage(client, Var.settingBase.logWatcher.statueMessage.RoundActive)
                    else -> {}//Nothing
                }
            }
            if(tempString.contains("LogGameMode: Display: Round Over")){
                val wonReason = tempString.substring(59).trim(')')
                Var.logger.log("回合结束，游戏${if(wonReason == "Objective") "胜利" else "失败"}")
                Utility.sendMessage(client,
                    if(wonReason == "Objective")
                        Var.settingBase.logWatcher.statueMessage.RoundWon
                    else
                        Var.settingBase.logWatcher.statueMessage.RoundFailed)
            }
        }
        fileStream.close()
    }
    else
        Var.logger.log("log文件于 ${logFile.path} 不存在, 将等待下一次检查")

    enum class GameStatues{
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