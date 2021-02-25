package no.nav.familie.ef.sak.service.steg

class StegException : RuntimeException {

    val logLevel: LogLevel

    constructor(cause: Exception, logLevel: LogLevel = LogLevel.ERROR) : super(cause) {
        this.logLevel = logLevel
    }

    constructor(message: String, cause: Exception, logLevel: LogLevel = LogLevel.ERROR) : super(message, cause) {
        this.logLevel = logLevel
    }

    constructor(message: String, logLevel: LogLevel = LogLevel.ERROR) : super(message) {
        this.logLevel = logLevel
    }

    enum class LogLevel {
        WARNING,
        ERROR
    }
}