package net.drabc.INSWatchDog

import kotlinx.coroutines.delay
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Vars.Var
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Message{
    private var messageIndex = 0
    suspend fun run(client: RconClient) {
        while(true) {
            Utility.sendMessage(client, snapString())
            delay((Var.settingBase.message.waitTime * 1000).toLong())
        }
    }

    private fun getRandomStr(aryList: List<String>): String {
        messageIndex++
        if(messageIndex >= aryList.size)
            messageIndex = 0
        return aryList[messageIndex]
    }

    private fun snapString(): String {
        var tempString = getRandomStr(Var.settingBase.message.msgList)
        if(tempString.contains("{time}")) {
            tempString = tempString.replace(
                "{time}",
                DateTimeFormatter.ofPattern(Var.settingBase.message.format.time).format(LocalDateTime.now())
            )
        }
        if(tempString.contains("{bestplayer}")){
            val tempPlayer = Var.playerList.maxBy{ p -> p.Score }
            if (tempPlayer != null) {
                tempString = tempString.replace(
                    "{bestplayer}",
                    if(tempPlayer.ID != 0){

                        if(tempPlayer.Score != 0.toLong()) {
                            tempPlayer.Name +
                                    Var.settingBase.message.format.lastBest.replace(
                                        "{0}",
                                        tempPlayer.Score.toString()
                                    )
                        }else
                            Var.settingBase.message.format.noBest
                    }
                    else
                        Var.settingBase.message.format.noBest)
            }
        }
        if(tempString.contains("{player}")){
            tempString = tempString.replace("{player}", Var.playerList.size.toString())
        }
        if(tempString.contains("{maps}")){
            tempString = tempString.replace("{maps}", Var.mapsName.size.toString())
        }
        return tempString
    }
}