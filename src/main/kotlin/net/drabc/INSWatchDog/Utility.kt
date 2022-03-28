package net.drabc.inswatchdog

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.runnable.LogWatcher
import net.drabc.inswatchdog.setting.*
import net.drabc.inswatchdog.vars.Player
import net.drabc.inswatchdog.vars.Var
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Utility {
    fun parseJson(name: String) : Any? {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<SettingBase> = moshi.adapter(SettingBase::class.java)
        Var.logger.log("尝试读取${name}", Logger.LogType.WARN)
        val stream = Files.newInputStream(Paths.get(name))
        stream.buffered().reader().use { reader -> return jsonAdapter.fromJson(reader.readText()) }
    }

    private fun keyWordReplace(word: String): String{
        require(word.isNotBlank()){Var.logger.log("输入值为空白", Logger.LogType.SEVERE)}
        var tempString = word
        if(tempString.contains("{time}")) {
            tempString = tempString.replace(
                "{time}",
                DateTimeFormatter.ofPattern(Var.settingBase.message.format.time).format(LocalDateTime.now())
            )
        }
        if(tempString.contains("{bestplayer}")){
            val tempPlayer = Var.playerList.maxByOrNull { p -> p.Score }
            if (tempPlayer != null) {
                tempString = tempString.replace(
                    "{bestplayer}",
                    if (tempPlayer.ID != 0 && tempPlayer.Score != 0.toLong()) {
                        tempPlayer.Name +
                                Var.settingBase.message.format.lastBest.replace(
                                    "{0}",
                                    tempPlayer.Score.toString()
                                )
                    } else
                        Var.settingBase.message.format.noBest
                )
            }
        }
        if(tempString.contains("{player}")){
            tempString = tempString.replace("{player}", Var.playerList.size.toString())
        }
        if(tempString.contains("{maps}")){
            tempString = tempString.replace("{maps}", Var.mapsName.size.toString())
        }
        if(tempString.contains("{difficult}")){
            tempString = tempString.replace("{difficult}", Var.nowDifficult.toString())
        }
        return tempString
    }

    fun sendCommand(client: RconClient, command: String, hide: Boolean = false): String{
        return try {
            val tempString = client.sendCommand(command)
            if (!hide) {
                Var.logger.log("已执行命令: $tempString")
            }
            tempString
        } catch (e: Exception){
            LogWatcher.gameStatue = LogWatcher.GameStatues.Dead
            "发送rcon命令失败${e.localizedMessage}"
        }
    }

    fun kickPlayer(client: RconClient, player: Player, reason: String) {
        sendCommand(client, "kick ${player.NetID} $reason")
        Var.logger.log("已踢出玩家: ${player.Name}[${player.NetID}]\t|\t${reason}\t|\t${player.IP}", Logger.LogType.WARN)
    }

    fun sendMessage(client: RconClient, message: String, header: Boolean = true): Boolean{
        return try {
            if(message.isNotBlank()) {
                val replacedMessage =
                    "${
                        if (header)
                            "[${Var.settingBase.message.msgHeader}]"
                        else
                            ""
                    } ${keyWordReplace(message)}"
                sendCommand(client, "say $replacedMessage", true)
                Var.logger.log("已发送消息: $replacedMessage")
                true
            }
            else
                false
        } catch (e: Exception){
            false
        }
    }

    fun changeDifficult(client: RconClient, flNum: Double){
        if(Var.nowDifficult != flNum){
            sendCommand(client, "gamemodeproperty AIDifficulty $flNum")

            if(Var.settingBase.difficult.renameServer) {
                sendCommand(client, "gamemodeproperty ServerHostname " +
                    Var.settingBase.setting.serverName.replace(
                        "{0}", String.format("%.1f", flNum)
                    )
                )
            }
            sendMessage(
                client, Var.settingBase.message.format.aiDifficult.replace(
                    "{0}", String.format("%.2f", flNum * 100)
                )
            )
            Var.nowDifficult = flNum
        }
    }

    fun getUserDir() : String{
        return System.getProperty("user.dir")
    }

    fun getGameStateByString(szTemp: String): LogWatcher.GameStatues{
        return when (szTemp) {
            "GameOver" -> LogWatcher.GameStatues.GameOver
            "LeavingMap" -> LogWatcher.GameStatues.LeavingMap
            "EnteringMap" -> LogWatcher.GameStatues.EnteringMap
            "LoadingAssets" -> LogWatcher.GameStatues.LoadingAssets
            "WaitingToStart" -> LogWatcher.GameStatues.WaitingToStart
            "GameStarting" -> LogWatcher.GameStatues.GameStarting
            "PreRound" -> LogWatcher.GameStatues.PreRound
            "RoundActive" -> LogWatcher.GameStatues.RoundActive
            "RoundWon" -> LogWatcher.GameStatues.RoundWon
            "PostRound" -> LogWatcher.GameStatues.PostRound
            "WaitingPostMatch" -> LogWatcher.GameStatues.WaitingPostMatch
            else -> LogWatcher.GameStatues.Undefine
        }
    }

    fun getMapList(client: RconClient){
        sendCommand(client, "maps", true).split('\n').forEach{
            if(it.isNotBlank())
                Var.mapsName.add(it.trim())
        }
    }

    fun getEmptySettingBase(): SettingBase{
        return SettingBase(
            CVars(listOf("bEnforceFriendlyFireReflect True", "MinimumTotalFriendlyFireDamageToReflect 300")),
            Ban(listOf(BanPlayer(74321234567898765, "I wanna kick ur ass")), "Bad guy", "KickKeyword"),
            difficult = Difficult(
                false, 1.0, 24, 0.1, 2,
                renameServer = false,
                failureDifficultTweak = false,
                failureDifficultReduce = 0.0,
                failureDifficultMessage = "Difficult reduce to {0}% cause failing"
            ),
            heartBeat = HeartBeat(
                false, "sandstorm.sh", 3, "Unknown OS {0}, abandon executing", "Local OS is {0}",
                "Game process dead, killing..."
            ),
            logWatcher = LogWatcher(
                StatueMessage(
                    "Game Over", "Round Over, best player is {bestplayer}", "Ready for mission",
                    "Go go go", "Nice try", "GGWP"
                ), 1
            ),
            message = Message(
                Format(
                    "AI tweak to {0}%",
                    "{0}th player {1} joined game",
                    "{0} points",
                    "{0}[{1}] left game",
                    "No one",
                    "Program stated",
                    "[MM dd HH:mm]"
                ),
                "Watchdog", listOf("Welcome to Ins server", "Please enjoy game here"), 40
            ),
            rcon = Rcon("127.0.0.1", "secret", 12345),
            saidCommand = SaidCommand("List all commands", 5, "help {0} change page | all {1} page", "Available commands:"),
            setting = Setting(
                "default", listOf("Scenario_Crossing_Checkpoint_Security", "Scenario_Farmhouse_Checkpoint_Security"),
                false, getUserDir(), "INS Server Difficulty[{0}]", 5, false
            ),
            soloBot = SoloBot(false, 8, 100, 1, 60, 500),
            syncPlayerList = SyncPlayerList(1)
        )
    }

    fun pressKeystoreContinue() {
        print("按任意键继续")
        Scanner(System.`in`).nextLine()
    }
}