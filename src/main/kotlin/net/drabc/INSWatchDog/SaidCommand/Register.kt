package net.drabc.inswatchdog.saidcommand

import net.drabc.inswatchdog.Logger
import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.runnable.LogWatcher
import net.drabc.inswatchdog.Utility
import net.drabc.inswatchdog.vars.Player
import net.drabc.inswatchdog.vars.Var
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object Register {
    val commands: MutableMap<String, BaseSaidCommand> = mutableMapOf()
    private fun getSaidCommand(name: String): BaseSaidCommand?{
        return commands[name]
    }
    private fun addSaidCommand(
        name: String,
        doResult: (RconClient, Player, CCommand) -> Boolean,
        helpMessage: String = "",
        argument: List<ArgumentItem> = emptyList()): Boolean{
        if(commands.containsKey(name)){
            Var.logger.log("注册命令${name}失败, 已存在此命令", Logger.LogType.SEVERE)
            return false
        }
        commands[name] = BaseSaidCommand(name,helpMessage,argument,doResult)
        Var.logger.log("已注册命令${name}", Logger.LogType.FINEST)
        return true
    }
    private fun addVoteCommand(
        name: String,
        doResult: (RconClient, Player, CCommand) -> Boolean,
        helpMessage: String = "",
        argument: List<ArgumentItem> = emptyList()): Boolean{
        if(commands.containsKey(name)){
            Var.logger.log("注册投票命令${name}失败, 已存在此命令", Logger.LogType.SEVERE)
            return false
        }
        commands[name] = BaseVoteCommand(name,helpMessage,argument,doResult)
        Var.logger.log("已注册投票命令${name}", Logger.LogType.FINEST)
        return true
    }

    private fun hasSaidCommand(name: String): Boolean{
        return commands.containsKey(name)
    }

    suspend fun execSaidCommand(client: RconClient, word: String, pPlayer: Player?): Boolean{
        if(pPlayer == null)
            return false
        val arg = CCommand(word.substring(1).trim().split(' '))
        if(!hasSaidCommand(arg[0]))
            return false
        getSaidCommand(arg[0])?.execute(client, pPlayer, arg)
        return true
    }

    fun registerSaidCommand(){
        Var.logger.log("开始注册聊天栏命令", Logger.LogType.WARN)

        addSaidCommand(
            "help",
            fun(client: RconClient, _:Player, arg: CCommand): Boolean{
                var flag = true
                val maxPage = max(1, ceil(commands.size.toDouble() / Var.settingBase.saidCommand.helpMaxLine.toDouble()).toInt())
                val nowPage = min(if(arg.argC() < 2) 1 else max(1, arg[1].toInt()), maxPage)
                flag = flag && Utility.sendMessage(client, Var.settingBase.saidCommand.helpMessageTop)
                for(i in
                    (nowPage-1)*Var.settingBase.saidCommand.helpMaxLine until
                    min(commands.size, nowPage*Var.settingBase.saidCommand.helpMaxLine)){
                    flag = flag && Utility.sendMessage(client, commands.toList()[i].second.getHelpInfo(), false)
                }
                flag = flag && Utility.sendMessage(client, "\n${Var.settingBase.saidCommand.helpMessageBottom
                        .replace("{0}", (nowPage+1).toString())
                        .replace("{1}",maxPage.toString())}", false)
                return flag
            },
            Var.settingBase.saidCommand.helpHelpMessage,
            listOf(ArgumentItem("Page", true)))

        addSaidCommand(
            "me",
            fun(client: RconClient, pPlayer:Player, _: CCommand): Boolean{
                Utility.sendMessage(client, "你是${pPlayer.Name}, ID是${pPlayer.ID}, SteamID是${pPlayer.NetID}, 共获得${pPlayer.Score}分")
                return true
            },
            "我是谁")

        addSaidCommand(
            "gamestats",
            fun(client: RconClient, _:Player, _: CCommand): Boolean{
                Utility.sendMessage(client, "现在游戏处于的状态是${LogWatcher.gameStatue}, " +
                        "游戏难度为${Var.nowDifficult}, BOT数${Var.nowBotCount}, 已胜利${LogWatcher.winRound}次, 失败${LogWatcher.failRound}次")
                return true
            },
            "游戏怎么样了")

        addVoteCommand(
            "rtv",
            fun(client: RconClient, _:Player, _: CCommand): Boolean{
                val mapName = Var.settingBase.setting.rtvMapList.shuffled().take(1)[0]
                Utility.sendMessage(client, "将在五秒后切换地图至[${mapName}]")
                Timer().schedule(5000){
                    Utility.sendCommand(client, "travelscenario $mapName")
                }
                return true
            },
            "投票随机切换地图"
        )
        Var.logger.log("聊天栏命令注册完毕")
    }
}