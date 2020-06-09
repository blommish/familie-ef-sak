package no.nav.familie.ef.sak.økonomi.exception

import no.nav.familie.kontrakter.felles.oppdrag.OppdragId

class UkjentOppdragException(val oppdragId : OppdragId, t: Throwable) : RuntimeException(t) {
}