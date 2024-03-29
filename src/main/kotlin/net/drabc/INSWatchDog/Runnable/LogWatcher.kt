@file:Suppress("BlockingMethodInNonBlockingContext")

package net.drabc.inswatchdog.runnable

import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.saidcommand.BaseVoteCommand
import net.drabc.inswatchdog.saidcommand.Register
import net.drabc.inswatchdog.Utility
import net.drabc.inswatchdog.vars.Var
import net.drabc.inswatchdog.vars.getPlayer
import java.io.File
import java.io.FileInputStream

class LogWatcher: BaseRunnable(Var.settingBase.logWatcher.waitTime, true) {
    override suspend fun execute(client: RconClient) = if(logFile.exists() && logFile.canRead()){
        FileInputStream(logFile).run{
            this.skip(filePointer.toLong())
            filePointer = logFile.length().toInt()
            this.reader().readLines().forEach {
                //查看状态
                //无视前30个无用字符
                if(it.length > 30) {
                    val tempString = it.substring(30)
                    when {
                        tempString.contains("LogGameMode: Display: State:") -> {
                            gameStatue = Utility.getGameStateByString(tempString.split("->")[1].trim())
                            Var.logger.log("游戏状态变为[${gameStatue}]")
                            when (gameStatue) {
                                GameStatues.GameOver -> Utility.sendMessage(client, Var.settingBase.logWatcher.statueMessage.GameOver)
                                GameStatues.PostRound -> Utility.sendMessage(client, Var.settingBase.logWatcher.statueMessage.PostRound)
                                GameStatues.RoundActive -> Utility.sendMessage(client, Var.settingBase.logWatcher.statueMessage.RoundActive)
                                GameStatues.PreRound -> Utility.sendMessage(client, Var.settingBase.logWatcher.statueMessage.PreRound)
                                GameStatues.LeavingMap -> {
                                    winRound = 0
                                    failRound = 0
                                    for(command in Register.commands) {
                                        (command as? BaseVoteCommand)?.votedPlayers = mutableListOf()
                                    }
                                    Var.playerList = mutableListOf()
                                    Var.settingBase.difficult.maxDifficult = Var.defaultSettingBase.difficult.maxDifficult
                                    Var.settingBase.difficult.minDifficult = Var.defaultSettingBase.difficult.minDifficult
                                }
                                else -> { }
                            }
                        }
                        tempString.contains("LogGameMode: Display: Round Over") -> {
                            val winState = tempString.substring(58).trim(')') == "Objective"
                            Var.logger.log("回合结束，游戏${if (winState) "胜利" else "失败"}")
                            Utility.sendMessage(
                                client,
                                if (winState)
                                    Var.settingBase.logWatcher.statueMessage.RoundWon
                                else
                                    Var.settingBase.logWatcher.statueMessage.RoundFailed
                            )
                            if(winState){
                                winRound++
                                if(Var.settingBase.difficult.failureDifficultTweak){
                                    Var.nowDifficult = Var.settingBase.difficult.maxDifficult.coerceAtLeast(
                                        Var.settingBase.difficult.minDifficult.coerceAtMost(Var.nowDifficult + Var.settingBase.difficult.failureDifficultReduce)
                                    )
                                    Utility.changeDifficult(client, Var.nowDifficult)
                                    Utility.sendMessage(client,
                                        Var.settingBase.difficult.failureDifficultMessage.replace("{0}",
                                            String.format("%.2f", Var.nowDifficult * 100)))
                                }
                            }
                            else{
                                failRound++
                                if(Var.settingBase.difficult.failureDifficultTweak){
                                    Var.nowDifficult = Var.settingBase.difficult.maxDifficult.coerceAtLeast(
                                        Var.settingBase.difficult.minDifficult.coerceAtMost(Var.nowDifficult - Var.settingBase.difficult.failureDifficultReduce)
                                    )
                                    Utility.changeDifficult(client, Var.nowDifficult)
                                    Utility.sendMessage(client,
                                        Var.settingBase.difficult.failureDifficultMessage.replace("{0}",
                                            String.format("%.2f", Var.nowDifficult * 100)))
                                }
                            }
                        }
                        tempString.contains("LogChat: Display: ") && tempString.contains(") Global Chat: ") -> {
                            val regex = Regex("""LogChat: Display:.*\([0-9]+\) Global Chat: """)
                            val playerSaid = regex.replace(tempString, "")
                            val playerInfo = regex.find(tempString)!!.value
                            val playerID = playerInfo.substring(playerInfo.length - 32, playerInfo.length - 15).toLong()
                            if (playerSaid.startsWith('!')){
                                //Register.execSaidCommand(client, playerSaid, Var.playerList.getPlayer(playerID))
                                for(it in Var.playerList){
                                    if(it.NetID == playerID){
                                        Register.execSaidCommand(client, playerSaid, it)
                                        break
                                    }
                                }
                                //Register.execSaidCommand(client, playerSaid, Var.playerList.getPlayer(playerID))
                            }
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
        var failRound: Int = 0
        var winRound: Int = 0
        private val logFile = File(Var.settingBase.setting.rootDir + "/Insurgency/Saved/Logs/Insurgency.log")
        private var filePointer : Int = 0
    }
    init {
        if(logFile.exists())
            filePointer = logFile.length().toInt()
    }
}
