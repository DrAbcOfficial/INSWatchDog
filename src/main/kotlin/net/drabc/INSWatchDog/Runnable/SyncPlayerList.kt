package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Player
import net.drabc.INSWatchDog.Vars.Var
import java.lang.Exception

class SyncPlayerList : BaseRunnable(Var.settingBase.syncPlayerList.waitTime){
    override suspend fun execute(client: RconClient) {
        getPlayerList(client, Utility.sendCommand(client, "listplayers", true))
    }

    private fun trimStr(szTemp: String): String {
        return szTemp.trim().trim('\t')
    }

    private fun strToLong(szTemp: String): Long{
        return try{
            trimStr(szTemp).toLong()
        }catch (e:Exception){
            0
        }
    }

    private fun getPlayerList(client: RconClient, szTemp: String){
        var tempList = szTemp.split('\n')
        if(tempList.size <= 2)
            return
        tempList = tempList[2].split('|')
        val tempPlayerList : MutableList<Player> = mutableListOf()
        for( i in 0 until tempList.size - (tempList.size % 5) - 1 step 5){
            val id = trimStr(tempList[i]).toInt()
            if(id != 0){
                val tempPlayer = Player(
                    id,
                    trimStr(tempList[i+1]),
                    strToLong(tempList[i+2]),
                    trimStr(tempList[i+3]),
                    strToLong(tempList[i+4])
                )
                var kickFlag = false
                Var.settingBase.ban.banPlayers.forEach {
                    if(it.id == tempPlayer.NetID) {
                        kickFlag = true
                        Utility.kickPlayer(client, tempPlayer, it.reason)
                        return
                    }
                }
                if(!kickFlag)
                    tempPlayerList.add(tempPlayer)

            }
        }

        if(Var.playerList != tempPlayerList) {
            //取交集
            val retireList = mutableListOf<Player>()
            Var.playerList.forEach { retireList.add(it) }
            retireList.retainAll(tempPlayerList)
            //加入
            //取差集
            val joinList = mutableListOf<Player>()
            tempPlayerList.forEach { joinList.add(it) }
            joinList.removeAll(retireList)
            joinList.forEachIndexed { i, player ->
                Utility.sendMessage(
                    client, Var.settingBase.message.format.join.replace(
                        "{0}", (retireList.size + 1 + i).toString()
                    ).replace(
                        "{1}", player.Name
                    )
                )
            }
            //离开
            val leaveList = mutableListOf<Player>()
            Var.playerList.forEach { leaveList.add(it) }
            leaveList.removeAll(retireList)
            leaveList.forEach {
                Utility.sendMessage(
                    client, Var.settingBase.message.format.leave.replace(
                        "{0}", it.Name
                    ).replace(
                        "{1}", it.NetID.toString()
                    )
                )
            }
        }
        Var.playerList = tempPlayerList
    }
}