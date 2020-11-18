package net.drabc.inswatchdog.rconclient.Exceptions
import java.io.IOException
class ConnectionException(cause: IOException?) : RuntimeException(cause)