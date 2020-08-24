import java.io.IOException

class ConnectionException : RuntimeException {

    var ioException: IOException? = null
        private set

    constructor() {}
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: IOException?) : super(message, cause) {
        ioException = cause
    }

    constructor(cause: IOException?) : super(cause) {
        ioException = cause
    }

    constructor(
        message: String?,
        cause: IOException?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace) {
        ioException = cause
    }
}