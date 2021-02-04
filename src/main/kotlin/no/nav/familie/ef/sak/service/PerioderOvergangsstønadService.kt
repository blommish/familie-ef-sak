package no.nav.familie.ef.sak.service

import no.nav.familie.ef.sak.exception.PdlNotFoundException
import no.nav.familie.ef.sak.integration.InfotrygdReplikaClient
import no.nav.familie.ef.sak.integration.PdlClient
import no.nav.familie.kontrakter.ef.infotrygd.InfotrygdPerioderOvergangsstønadRequest
import no.nav.familie.kontrakter.felles.ef.PeriodeOvergangsstønad
import no.nav.familie.kontrakter.felles.ef.PerioderOvergangsstønadRequest
import no.nav.familie.kontrakter.felles.ef.PerioderOvergangsstønadResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PerioderOvergangsstønadService(private val infotrygdReplikaClient: InfotrygdReplikaClient,
                                     private val pdlClient: PdlClient) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Henter perioder fra infotrygd for en person
     * SKal hente perioder fra ef-sak også i fremtiden
     */
    fun hentPerioder(request: PerioderOvergangsstønadRequest): PerioderOvergangsstønadResponse {
        val personIdenter = hentPersonIdenter(request)
        val infotrygdRequest = InfotrygdPerioderOvergangsstønadRequest(personIdenter, request.fomDato, request.tomDato)
        val infotrygdPerioder = infotrygdReplikaClient.hentPerioderOvergangsstønad(infotrygdRequest)
        val perioder = infotrygdPerioder.perioder.sortedBy { it.fomDato }.map {
            val tomDato = it.opphørsdato?.let { opphørsdato -> if (opphørsdato.isBefore(it.tomDato)) opphørsdato else it.tomDato }
                          ?: it.tomDato
            PeriodeOvergangsstønad(personIdent = it.personIdent,
                                   fomDato = it.fomDato,
                                   tomDato = tomDato,
                                   datakilde = PeriodeOvergangsstønad.Datakilde.INFOTRYGD)
        }
        return PerioderOvergangsstønadResponse(slåSammenSammenhengendePerioder(perioder))
    }

    private fun slåSammenSammenhengendePerioder(perioder: List<PeriodeOvergangsstønad>): List<PeriodeOvergangsstønad> {
        val mergedePerioder = mutableListOf<PeriodeOvergangsstønad>()
        perioder.forEach {
            if (mergedePerioder.isEmpty()) {
                mergedePerioder.add(it)
            } else {
                val last = mergedePerioder.last()
                if (last.tomDato.plusDays(1) == it.fomDato) {
                    mergedePerioder[mergedePerioder.size - 1] = last.copy(tomDato = it.tomDato)
                } else {
                    mergedePerioder.add(it)
                }
            }
        }
        return mergedePerioder
    }

    private fun hentPersonIdenter(request: PerioderOvergangsstønadRequest): Set<String> {
        return try {
            pdlClient.hentPersonidenter(request.personIdent, true).identer.map { it.ident }.toSet()
        } catch (e: PdlNotFoundException) {
            logger.warn("Finner ikke person, returnerer personIdent i request")
            setOf(request.personIdent)
        }
    }

}