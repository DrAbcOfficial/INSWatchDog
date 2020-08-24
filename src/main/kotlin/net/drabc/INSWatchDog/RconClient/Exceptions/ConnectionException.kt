package net.drabc.INSWatchDog.RconClient.Exceptions
import java.io.IOException
class ConnectionException(cause: IOException?) : RuntimeException(cause) {
    private var ioException: IOException? = cause
}