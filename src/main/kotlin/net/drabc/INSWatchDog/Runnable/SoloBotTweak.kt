package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Var
import kotlin.math.max
import kotlin.math.min

class SoloBotTweak : BaseRunnable(){
    override fun execute(client: RconClient) {
        if(oldPlayerNumber != Var.playerList.size && Var.playerList.size != 0) {
            soloTweak(client, Var.playerList.size)
            oldPlayerNumber = Var.playerList.size
        }
    }
    private fun soloTweak(client: RconClient, nowPlayer: Int){
        var intNum = Var.settingBase.soloBot.maxBots / Var.settingBase.soloBot.fullSetPlayer * nowPlayer
        if (nowPlayer > Var.settingBase.soloBot.fullSetPlayer)
            intNum = Var.settingBase.soloBot.maxBots
        else {
            intNum = max(Var.settingBase.soloBot.minBots, intNum)
            intNum = min(Var.settingBase.soloBot.maxBots, intNum)
        }
        if(oldBotNumber != intNum) {
            oldBotNumber = intNum
            Utility.sendCommand(client, "gamemodeproperty SoloEnemies $intNum")
        }
    }
    companion object{
        private var oldPlayerNumber : Int = 0
        private var oldBotNumber : Int = 0
    }
}