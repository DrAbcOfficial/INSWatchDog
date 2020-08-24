package net.drabc.INSWatchDog

import kotlinx.coroutines.delay
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Vars.Var
import kotlin.math.max
import kotlin.math.min

object SoloBotTweak {
    private var oldPlayerNumber : Int = 0
    suspend fun run(client: RconClient) {
        while(true) {
            if(oldPlayerNumber != Var.playerList.size && Var.playerList.size != 0) {
                SoloTwick(client, Var.playerList.size)
                oldPlayerNumber = Var.playerList.size
            }
           delay((Var.settingBase.setting.waitTime * 1000).toLong())
        }
    }

    private fun SoloTwick(client: RconClient, nowPlayer: Int){
        var intNum = Var.settingBase.soloBot.maxBots / Var.settingBase.soloBot.fullSetPlayer * nowPlayer
        if (nowPlayer > Var.settingBase.soloBot.fullSetPlayer)
            intNum = Var.settingBase.soloBot.maxBots
        else {
            intNum = max(Var.settingBase.soloBot.minBots, intNum)
            intNum = min(Var.settingBase.soloBot.maxBots, intNum)
        }
        Utility.sendCommand(client,"gamemodeproperty SoloEnemies $intNum")
    }
}