package no.nav.familie.ef.sak.service.steg

/**
 * Skal kun kastes av steg som kjøres maskinellt av tasks
 */
class RekjørStegException : RuntimeException {

    constructor() : super()
    constructor(cause: Exception) : super(cause)
    constructor(message: String, cause: Exception) : super(message, cause)
    constructor(message: String) : super(message)
}