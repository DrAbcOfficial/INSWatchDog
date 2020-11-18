package net.drabc.inswatchdog.saidcommand

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.Utility
import net.drabc.inswatchdog.vars.Player
import net.drabc.inswatchdog.vars.Var

open class BaseVoteCommand(
    override val commandWord: String,
    override val helpMessage: String,
    override val argument: List<ArgumentItem>,
    override val doResult: (RconClient, Player, CCommand) -> Boolean,
    private val votedRatio:Double = 0.6
): BaseSaidCommand(commandWord, helpMessage, argument, doResult) {

    var votedPlayers: MutableList<Player> = mutableListOf()
    override suspend fun execute(client: RconClient, pPlayer: Player, arg: CCommand) = coroutineScope {
        launch {
            for(player in votedPlayers){
                if(!Var.playerList.contains(player))
                    votedPlayers.remove(player)
            }

            val successNum = (Var.playerList.count() * votedRatio).toInt()
            if(votedPlayers.count() >= successNum){
                Utility.sendMessage(client, "投票${commandWord} [${helpMessage}]已通过")
                doResult(client, pPlayer, arg)
            }
            else{
                votedPlayers.add(pPlayer)
                Utility.sendMessage(client, "投票${commandWord} [${helpMessage}] ${votedPlayers.count()}/${successNum}")
            }
        }
    }
}