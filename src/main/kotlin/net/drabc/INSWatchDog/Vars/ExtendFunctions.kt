package net.drabc.INSWatchDog.Vars

fun MutableList<Player>.getPlayer(netID: Long): Player?{
    forEach {
        if(it.NetID == netID)
            return it
    }
    return null
}