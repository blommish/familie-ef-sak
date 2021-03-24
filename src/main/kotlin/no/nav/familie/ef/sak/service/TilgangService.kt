package no.nav.familie.ef.sak.service

import no.nav.familie.ef.sak.api.ManglerTilgang
import no.nav.familie.ef.sak.config.RolleConfig
import no.nav.familie.ef.sak.integration.FamilieIntegrasjonerClient
import no.nav.familie.ef.sak.service.steg.BehandlerRolle
import no.nav.familie.ef.sak.sikkerhet.SikkerhetContext
import no.nav.familie.ef.sak.util.loggTid
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TilgangService(private val integrasjonerClient: FamilieIntegrasjonerClient,
                     private val personService: PersonService,
                     private val behandlingService: BehandlingService,
                     private val fagsakService: FagsakService,
                     private val rolleConfig: RolleConfig) {

    fun validerTilgangTilPersonMedBarn(personIdent: String) {
        loggTid(this::class, "validerTilgangTilPersonMedBarn") {

            val barnOgForeldre = loggTid(this::class, "validerTilgangTilPersonMedBarn", "hentIdenterForBarnOgForeldre") {personService.hentIdenterForBarnOgForeldre(forelderIdent = personIdent)}

            loggTid(this::class, "validerTilgangTilPersonMedBarn", "sjekkTilgangTilPersoner") {
                integrasjonerClient.sjekkTilgangTilPersoner(barnOgForeldre).forEach {
                    if (!it.harTilgang) {
                        throw ManglerTilgang("Saksbehandler ${SikkerhetContext.hentSaksbehandler()} " +
                                             "har ikke tilgang til $personIdent eller dets barn")
                    }
                }
            }
        }
    }

    fun validerTilgangTilBehandling(behandlingId: UUID) {
        loggTid(this::class, "validerTilgangTilBehandling") {
            val fagsakId = behandlingService.hentBehandling(behandlingId).fagsakId
            validerTilgangTilFagsak(fagsakId)
        }
    }

    fun validerHarSaksbehandlerrolle() {
        validerTilgangTilRolle(BehandlerRolle.SAKSBEHANDLER)
    }

    fun validerTilgangTilRolle(minimumsrolle: BehandlerRolle) {
        if (!harTilgangTilRolle(minimumsrolle)) {
            throw ManglerTilgang("Saksbehandler ${SikkerhetContext.hentSaksbehandler()} har ikke tilgang " +
                                 "til å utføre denne operasjonen som krever minimumsrolle $minimumsrolle")
        }
    }

    fun harTilgangTilRolle(minimumsrolle: BehandlerRolle): Boolean {
        return SikkerhetContext.harTilgangTilGittRolle(rolleConfig, minimumsrolle)
    }

    fun validerTilgangTilFagsak(fagsakId: UUID) {
        loggTid(this::class, "validerTilgangTilFagsak") {
            val personIdent = fagsakService.hentFagsak(fagsakId).hentAktivIdent()
            validerTilgangTilPersonMedBarn(personIdent)
        }
    }
}