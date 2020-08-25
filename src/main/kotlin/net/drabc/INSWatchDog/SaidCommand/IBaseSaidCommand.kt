package net.drabc.INSWatchDog.SaidCommand

import kotlinx.coroutines.Job
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Vars.Player

interface IBaseSaidCommand{
    val commandWord: String
    val helpMessage: String
    val argument: List<ArgumentItem>
    val doResult:(RconClient, Player, CCommand) -> Boolean
    fun getHelpInfo(): String
    suspend fun execute(client: RconClient, pPlayer: Player, arg: CCommand): Job
}