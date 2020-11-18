package net.drabc.inswatchdog.runnable

import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.Utility
import net.drabc.inswatchdog.vars.Var

class Message : BaseRunnable(Var.settingBase.message.waitTime, true){
    override suspend fun execute(client: RconClient) {
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