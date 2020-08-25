package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Var
import kotlin.math.max
import kotlin.math.min

class DifficultTweak : BaseRunnable(){
    override fun execute(client: RconClient) {
        if(oldPlayerNumber != Var.playerList.size && Var.playerList.size != 0) {
            difficultTweak(client, Var.playerList.size)
            oldPlayerNumber = Var.playerList.size
        }
    }
    private fun difficultTweak(client: RconClient, nowPlayer: Int) {
        var flNum = Var.settingBase.difficult.maxDifficult / Var.settingBase.difficult.maxToScale * nowPlayer
        if (nowPlayer > Var.settingBase.difficult.maxToScale)
            flNum = Var.settingBase.difficult.maxDifficult
        else {
            flNum = max(Var.settingBase.difficult.minDifficult, flNum)
            flNum = min(Var.settingBase.difficult.maxDifficult, flNum)
        }
        if(Var.nowDifficult != flNum){
            Utility.sendCommand(
                client,
                "gamemodeproperty AIDifficulty $flNum"
            )
            Utility.sendCommand(
                client,
                "gamemodeproperty ServerHostname " +
                        Var.settingBase.setting.serverName.replace(
                            "{0}", String.format("%.1f", flNum)
                        )
            )
            Utility.sendMessage(
                client, Var.settingBase.message.format.aiDifficult.replace(
                    "{0}", String.format("%.2f", flNum * 100)
                )
            )
            Var.nowDifficult = flNum
        }
    }
    companion object{
        private var oldPlayerNumber : Int = 0
    }
}