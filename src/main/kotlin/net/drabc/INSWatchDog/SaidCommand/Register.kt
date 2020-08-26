package net.drabc.INSWatchDog.SaidCommand

import net.drabc.INSWatchDog.Logger
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Runnable.LogWatcher
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Player
import net.drabc.INSWatchDog.Vars.Var
import kotlin.math.max
import kotlin.math.min

object Register {
    private val commands: MutableMap<String, BaseSaidCommand> = mutableMapOf()
    private fun getSaidCommand(name: String): BaseSaidCommand?{
        return commands[name]
    }
    private fun addSaidCommand(
        name: String,
        doResult: (RconClient, Player, CCommand) -> Boolean,
        helpMessage: String = "",
        argument: List<ArgumentItem> = emptyList()): Boolean{
        if(commands.containsKey(name)){
            Var.logger.log("注册命令${name}失败, 已存在此命令", Logger.LogType.WARN)
            return false
        }
        commands[name] = BaseSaidCommand(name,helpMessage,argument,doResult)
        Var.logger.log("已注册命令${name}")
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
                val maxPage = max(1, commands.size / Var.settingBase.saidCommand.helpMaxLine)
                val nowPage = min(if(arg.argC() < 2) 1 else max(1, arg[1].toInt()), maxPage)
                flag = flag && Utility.sendMessage(client, Var.settingBase.saidCommand.helpMessageTop)
                for(i in
                    (nowPage-1)*Var.settingBase.saidCommand.helpMaxLine until
                    min(commands.size, nowPage*Var.settingBase.saidCommand.helpMaxLine)){
                    flag = flag && Utility.sendMessage(client, commands.toList()[i].second.getHelpInfo(), false)
                }
                flag = flag && Utility.sendMessage(client, "\n${Var.settingBase.saidCommand.helpMessageButtom
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
            "gamestatue",
            fun(client: RconClient, pPlayer:Player, _: CCommand): Boolean{
                Utility.sendMessage(client, "现在游戏处于的状态是${LogWatcher.gameStatue}, 游戏难度为${Var.nowDifficult}, BOT数${Var.nowBotCount}")
                return true
            },
            "游戏怎么样了")
        Var.logger.log("聊天栏命令注册完毕")
    }
}