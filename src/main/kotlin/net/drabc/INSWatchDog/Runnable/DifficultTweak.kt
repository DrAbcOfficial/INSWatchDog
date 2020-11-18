package net.drabc.INSWatchDog.Runnable

import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Utility
import net.drabc.INSWatchDog.Vars.Var
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
        var flNum = Var.nowMaxDifficult[0] / Var.settingBase.difficult.maxToScale * nowPlayer
        if (nowPlayer > Var.settingBase.difficult.maxToScale)
            flNum = Var.nowMaxDifficult[0]
        else {
            flNum = max(Var.nowMaxDifficult[1], flNum)
            flNum = min(Var.nowMaxDifficult[0], flNum)
        }
        Utility.changeDifficult(client, flNum)
    }
    companion object{
        private var oldPlayerNumber : Int = 0
    }
}