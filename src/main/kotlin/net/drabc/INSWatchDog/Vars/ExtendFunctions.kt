package net.drabc.inswatchdog.vars

fun MutableList<Player>.getPlayer(netID: Long): Player?{
    forEach {
        if(it.NetID == netID)
            return it
    }
    return null
}