package no.nav.familie.ef.sak.no.nav.familie.ef.sak.api

import no.nav.familie.ef.sak.no.nav.familie.ef.sak.Testsøknad
import no.nav.familie.ef.sak.service.BehandlingService
import no.nav.familie.ef.sak.service.steg.StegService
import no.nav.familie.kontrakter.ef.sak.SakRequest
import no.nav.familie.kontrakter.ef.søknad.SøknadMedVedlegg
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(path = ["/api/external/sak/"])
@Unprotected
@Validated
class TestSakController(private val behandlingService: BehandlingService, private val stegService: StegService) {

    @PostMapping("dummy")
    fun dummy(): UUID {
        val sak = SakRequest(SøknadMedVedlegg(Testsøknad.søknad, emptyList()), "123", "321")
        //TODO Dette steget må trigges et annet sted når vi har satt flyten for opprettelse av behandling.
        // Trigger den her midlertidig for å kunne utføre inngangsvilkår-steget
        val behandling = behandlingService.mottaSakOvergangsstønad(sak, emptyMap())
        stegService.håndterRegistrerOpplysninger(behandling, "")
        return behandling.id
    }

}