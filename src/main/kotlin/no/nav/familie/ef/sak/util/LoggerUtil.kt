package no.nav.familie.ef.sak.util

import no.nav.familie.ef.sak.sikkerhet.SikkerhetContext
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

val logger = LoggerFactory.getLogger("Timer")

fun <T> loggTid(clazz: KClass<out Any>, method: String, callName: String = "", fn: () -> T): T {
    val start = System.currentTimeMillis()
    val result = fn.invoke()
    if (SikkerhetContext.hentSaksbehandler() == "Z994230") {
        val time = System.currentTimeMillis() - start
        logger.info("Timer ${if (time > 50) "SLOW" else ""} - class=${clazz.simpleName} method=$method call=$callName time=$time")
    }
    return result
}
