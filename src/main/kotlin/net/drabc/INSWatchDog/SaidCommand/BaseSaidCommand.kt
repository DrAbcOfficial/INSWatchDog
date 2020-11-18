package net.drabc.inswatchdog.saidcommand

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.drabc.inswatchdog.rconclient.RconClient
import net.drabc.inswatchdog.vars.Player

open class BaseSaidCommand(
    override val commandWord: String,
    override val helpMessage: String,
    override val argument: List<ArgumentItem>,
    override val doResult: (RconClient, Player, CCommand) -> Boolean
): IBaseSaidCommand {
    override fun getHelpInfo(): String {
        var tempString = "$commandWord "
        argument.forEach {
            tempString += if(it.optional) "[${it.name}] " else "<${it.name}> "
        }
        return "$tempString || $helpMessage"
    }

    override suspend fun execute(client: RconClient, pPlayer: Player, arg: CCommand) = coroutineScope {
        launch {
            doResult(client, pPlayer, arg)
        }
    }
}