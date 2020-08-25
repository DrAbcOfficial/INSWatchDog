package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Var

class Message : BaseRunnable(Var.settingBase.message.waitTime){
    override fun execute(client: RconClient) {
        if(Var.playerList.size > 0)
            Utility.sendMessage(client, getRandomStr(Var.settingBase.message.msgList))
    }
    private fun getRandomStr(aryList: List<String>): String {
        messageIndex++
        if(messageIndex >= aryList.size)
            messageIndex = 0
        return aryList[messageIndex]
    }

    companion object{
        private var messageIndex = 0
    }
}