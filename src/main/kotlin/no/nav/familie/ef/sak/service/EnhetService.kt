package no.nav.familie.ef.sak.service

import no.nav.familie.ef.sak.integration.Norg2Client
import no.nav.familie.ef.sak.integration.PdlClient
import no.nav.familie.kontrakter.felles.arbeidsfordeling.Enhet
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class EnhetService(private val norg2Client: Norg2Client, private val pdlClient: PdlClient) {

    @Cacheable("norg2_enheter")
    fun hentEnheter(): Map<String, Enhet>{
        return norg2Client.hentEnheter().associateBy { it.enhetId }
    }

    fun hentEnhetFraEnhetId(enhetId: String) = hentEnheter()[enhetId]

    fun hentEnhet(ident: String): Enhet {
        val geografiskTilknytning = pdlClient.hentGeografiskTilknytning(ident)
        return norg2Client.hentEnhet(geografiskTilknytning)
    }

}