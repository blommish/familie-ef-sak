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

    @Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..)) " +
              "|| execution(public * no.nav.familie.http.client.AbstractRestClient+.*(..))") fun monitor() {
    }

    @Around("monitor()") fun profile(pjp: ProceedingJoinPoint): Any? {
        val logger = LoggerFactory.getLogger(pjp.signature.declaringType)
        val start = System.currentTimeMillis()
        if (SikkerhetContext.hentSaksbehandler() == "Z994230") {
            logger.info("Timer start")
        }
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
                    logger.info("Timer aop SLOW - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time=$elapsedTime - a=${callingClass?.fileName} ${callingClass?.methodName} $methodName")
                } else {
                    logger.info("Timer aop - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time=$elapsedTime - a=${callingClass?.fileName} ${callingClass?.methodName} $methodName")
                }
            }
        }
    }

    @Pointcut("execution(* org.springframework.jdbc.datasource.DataSourceUtils.*(..))")
    fun monitor3() {
    }

    @Around("monitor3()")
    fun profile3(pjp: ProceedingJoinPoint): Any? {
        return pjp.proceed()
    }


    @Pointcut("execution(* org.springframework.jdbc.core.JdbcOperations.*(..))")
    fun monitor2() {
    }

    @Around("monitor2()") fun profile2(pjp: ProceedingJoinPoint): Any? {
        val logger = LoggerFactory.getLogger(pjp.signature.declaringType)
        val start = System.currentTimeMillis()
        try {
            return pjp.proceed()
        } finally {
            if (SikkerhetContext.hentSaksbehandler() == "Z994230") {
                val elapsedTime = System.currentTimeMillis() - start
                logger.info("Timer aop - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time=$elapsedTime")
            }
        }
    }
}