package no.nav.familie.ef.sak.integration

import no.nav.familie.http.client.AbstractPingableRestClient
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.kontrakter.felles.arbeidsfordeling.Enhet
import no.nav.familie.kontrakter.felles.simulering.FagOmrådeKode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI


@Service
class Norg2Client(@Value("\${NORG2_URL}")
                  private val norg2Uri: URI,
                  @Qualifier("utenAuth")
                  restOperations: RestOperations)
    : AbstractPingableRestClient(restOperations, "norg2") {

    private val hentEnheterUri: URI =
            UriComponentsBuilder.fromUri(norg2Uri).pathSegment("api/v1/enhet").build().toUri()

    fun hentEnheter(): List<Enhet> {
        return getForEntity<Ressurs<List<Enhet>>>(hentEnheterUri).data!!
    }

    fun hentEnhet(geografiskOmråde: String): Enhet {
        return getForEntity<Ressurs<Enhet>>(UriComponentsBuilder.fromUri(norg2Uri)
                                                    .pathSegment("api/v1/enhet/navkontor/$geografiskOmråde")
                                                    .build()
                                                    .toUri()).data!!
    }

    override val pingUri: URI
        get() = UriComponentsBuilder.fromUri(norg2Uri).pathSegment("api/ping").build().toUri()

}