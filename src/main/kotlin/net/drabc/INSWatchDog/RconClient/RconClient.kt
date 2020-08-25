package net.drabc.INSWatchDog.RconClient

import net.drabc.INSWatchDog.RconClient.Exceptions.ConnectionException
import net.drabc.INSWatchDog.RconClient.Exceptions.AuthFailureException
import net.drabc.INSWatchDog.RconClient.Exceptions.RconClientException
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger


class RconClient {
    private var socket: Socket? = null
    private val currentRequestId: AtomicInteger = AtomicInteger(1)
    private var status = Status.Ready
    private var charsets = StandardCharsets.UTF_8
    private val authFailId = -1

    enum class Status {
        Ready, Connected, Working, Disconnected
    }

    fun connect(host: String, port: Int, password: String) {
        try {
            this.socket = Socket(host, port)
            val res: RconPacket = send(RconPacket.PacketAuth, password.toByteArray())
            if (res.requestId == authFailId) {
                throw AuthFailureException()
            }
            status = Status.Connected
        } catch (ex: IOException) {
            throw ConnectionException(ex)
        }
    }

    private fun send(type: Int, payload: ByteArray): RconPacket {
        try {
            write(socket!!.getOutputStream(), currentRequestId, type, payload)
        }
        catch (se: SocketException) {
            disconnect()
            throw se
        }
        return read(socket!!.getInputStream())
    }

    private fun write(out: OutputStream, requestId: AtomicInteger, type: Int, payload: ByteArray) {
        try {
            val bodyLength = 10 + payload.size
            val packetLength = 4 + bodyLength
            val buffer = ByteBuffer.allocate(packetLength)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            buffer.putInt(bodyLength)
            buffer.putInt(requestId.toInt())
            buffer.putInt(type)
            buffer.put(payload)
            buffer.put(0.toByte())
            buffer.put(0.toByte())
            out.write(buffer.array())
            out.flush()
        }
        catch (ex: IOException) {
            throw ConnectionException(ex)
        }
    }

    private fun read(inStream: InputStream): RconPacket {
        return try {
            val header = ByteArray(4 * 3)
            if (inStream.read(header) < 1) throw RconClientException("Wrong packet received")
            val buffer = ByteBuffer.wrap(header)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            val length = buffer.int
            val requestId = buffer.int
            buffer.int
            val payload = ByteArray(length - 4 - 4 - 2)
            val dis = DataInputStream(inStream)
            dis.readFully(payload)
            if (dis.read(ByteArray(2)) <= 0) throw RconClientException("Wrong packet received")
            RconPacket(requestId, payload)
        }
        catch (e: BufferUnderflowException) { throw RconClientException("Cannot read the whole packet") }
        catch (e: EOFException) { throw RconClientException("Cannot read the whole packet") }
        catch (ex: IOException) { throw ConnectionException(ex) }
    }

    private fun disconnect() {
        try {
            socket!!.close()
            status = Status.Disconnected
        }
        catch (ex: IOException) {
            throw ConnectionException(ex)
        }
    }

    fun sendCommand(payload: String): String {
        return try {
            require(!payload.isBlank()) { "Payload can't be null or empty" }
            status = Status.Working
            val response = send(RconPacket.PacketExecCommand, payload.toByteArray())
            status = Status.Connected
            return String(response.payload, charsets)
        }
        catch (ex: IOException) {
            throw ConnectionException(ex)
        }
    }
}