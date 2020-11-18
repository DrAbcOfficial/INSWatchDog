package net.drabc.inswatchdog.saidcommand

import kotlinx.coroutines.Job
import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.vars.Player

interface IBaseSaidCommand{
    val commandWord: String
    val helpMessage: String
    val argument: List<ArgumentItem>
    val doResult:(RconClient, Player, CCommand) -> Boolean
    fun getHelpInfo(): String
    suspend fun execute(client: RconClient, pPlayer: Player, arg: CCommand): Job
}