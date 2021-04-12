package no.nav.familie.ef.sak.api.gui

import no.nav.familie.ef.sak.api.dto.*
import no.nav.familie.ef.sak.integration.PdlClient
import no.nav.familie.ef.sak.service.*
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.kontrakter.felles.arbeidsfordeling.Enhet
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping(path = ["/api/personopplysninger"], produces = [APPLICATION_JSON_VALUE])
@ProtectedWithClaims(issuer = "azuread")
@Validated
class PersonopplysningerController(private val personopplysningerService: PersonopplysningerService,
                                   private val tilgangService: TilgangService,
                                   private val behandlingService: BehandlingService,
                                   private val fagsakService: FagsakService,
                                   private val enhetService: EnhetService) {

    @PostMapping
    fun personopplysninger(@RequestBody personIdent: PersonIdentDto): Ressurs<PersonopplysningerDto> {
        tilgangService.validerTilgangTilPersonMedBarn(personIdent.personIdent)
        return Ressurs.success(personopplysningerService.hentPersonopplysninger(personIdent.personIdent))
    }

    @GetMapping("/behandling/{behandlingId}")
    fun personopplysninger(@PathVariable behandlingId: UUID): Ressurs<PersonopplysningerDto> {
        tilgangService.validerTilgangTilBehandling(behandlingId)
        val aktivIdent = behandlingService.hentAktivIdent(behandlingId)
        return Ressurs.success(personopplysningerService.hentPersonopplysninger(aktivIdent))
    }

    @GetMapping("/fagsak/{fagsakId}")
    fun personopplysningerFraFagsakId(@PathVariable fagsakId: UUID): Ressurs<PersonopplysningerDto> {
        tilgangService.validerTilgangTilFagsak(fagsakId)
        val aktivIdent = fagsakService.hentAktivIdent(fagsakId)
        return Ressurs.success(personopplysningerService.hentPersonopplysninger(aktivIdent))
    }

    @GetMapping("/geografisk-tilknytning/behandling/{behandlingId}")
    fun geografiskTilknytning(@PathVariable behandlingId: UUID): Ressurs<Enhet> {
        tilgangService.validerTilgangTilBehandling(behandlingId)
        val aktivIdent = behandlingService.hentAktivIdent(behandlingId)
        return Ressurs.success(enhetService.hentEnhet(aktivIdent))
    }

}
