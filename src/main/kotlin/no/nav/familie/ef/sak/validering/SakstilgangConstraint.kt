package no.nav.familie.ef.sak.validering

import no.nav.familie.ef.sak.validering.Sakstilgang
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Suppress("unused")
@MustBeDocumented
@Constraint(validatedBy = [Sakstilgang::class])
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SakstilgangConstraint(val message: String = "Ikke tilgang til sak",
                                       val groups: Array<KClass<*>> = [],
                                       val payload: Array<KClass<out Payload>> = [])
