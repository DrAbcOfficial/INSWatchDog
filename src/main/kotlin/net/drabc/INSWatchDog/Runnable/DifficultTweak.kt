package net.drabc.inswatchdog.runnable

import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.Utility
import net.drabc.inswatchdog.vars.Var
import kotlin.math.max
import kotlin.math.min

class DifficultTweak : BaseRunnable(_forceExec = true){
    override suspend fun execute(client: RconClient) {
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
        Utility.changeDifficult(client, flNum)
    }
    companion object{
        private var oldPlayerNumber : Int = 0
    }
}