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
        try {
            return pjp.proceed()
        } finally {
            if (SikkerhetContext.hentSaksbehandler() == "Z994230") {
                val elapsedTime = System.currentTimeMillis() - start
                logger.info("Timer aop - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time:$elapsedTime")
            }
        }
    }

    @Pointcut("execution(* org.springframework.jdbc.core.JdbcOperations.*(..))")
    fun monitor2() {}
    @Around("monitor2()") fun profile2(pjp: ProceedingJoinPoint): Any? {
        val logger = LoggerFactory.getLogger(pjp.signature.declaringType)
        val start = System.currentTimeMillis()
        try {
            return pjp.proceed()
        } finally {
            if (SikkerhetContext.hentSaksbehandler() == "Z994230") {
                val elapsedTime = System.currentTimeMillis() - start
                logger.info("Timer aop - ${pjp.signature.declaringType.simpleName}.${pjp.signature.name} - time:$elapsedTime")
            }
        }
    }
}