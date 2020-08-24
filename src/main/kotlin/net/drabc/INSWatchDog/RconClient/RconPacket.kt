package net.drabc.INSWatchDog.RconClient

internal class RconPacket(val requestId: Int, val type: Int, val payload: ByteArray) {

    companion object {
        const val PacketExecCommand = 2
        const val PacketAuth = 3
    }
}