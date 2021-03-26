package no.nav.familie.ef.sak.config

import no.nav.familie.ef.sak.sikkerhet.SikkerhetContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration


@Configuration
@Aspect
class LoggingConfig {

    @Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))") fun monitor() {
    }

    @Around("monitor()") fun profile(pjp: ProceedingJoinPoint): Any? {
        val logger = LoggerFactory.getLogger(pjp.signature.declaringType)
        val start = System.currentTimeMillis()
        try {
            return pjp.proceed()
        } finally {
            if (SikkerhetContext.hentSaksbehandler() == "Z994230") {
                val elapsedTime = System.currentTimeMillis() - start
                val stackTrace = Thread.currentThread().stackTrace
                val callingClass = stackTrace.firstOrNull {
                    it.className != "no.nav.familie.ef.sak.config.LoggingConfig" && it.className.startsWith("no.nav.familie.ef")
                }
                val methodName = stackTrace.indexOf(callingClass).takeIf { it > -1 }?.let { stackTrace[it - 1].methodName }

                if (elapsedTime > 50) {
                    logger.info("Timer aop slow - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time=$elapsedTime - ${callingClass?.fileName} ${callingClass?.methodName} $methodName")
                } else {
                    logger.info("Timer aop - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time=$elapsedTime - ${callingClass?.fileName} ${callingClass?.methodName} $methodName")
                }
            }
        }
    }

    @Pointcut("execution(public * no.nav.familie.http.client.AbstractRestClient+.*(..))") fun monitor2() {
    }

    @Around("monitor2()") fun profile2(pjp: ProceedingJoinPoint): Any? {
        val logger = LoggerFactory.getLogger(pjp.signature.declaringType)
        val start = System.currentTimeMillis()
        try {
            return pjp.proceed()
        } finally {
            if (SikkerhetContext.hentSaksbehandler() == "Z994230") {
                val elapsedTime = System.currentTimeMillis() - start
                if (elapsedTime > 50) {
                    logger.info("Timer aop slow - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time=$elapsedTime")
                } else {
                    logger.info("Timer aop - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time=$elapsedTime")
                }
            }
        }
    }
}