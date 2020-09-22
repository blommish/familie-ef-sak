package no.nav.familie.ef.sak.api.oppgave

import no.nav.familie.ef.sak.service.OppgaveService
import no.nav.familie.ef.sak.util.RessursUtils.illegalState
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.kontrakter.felles.oppgave.FinnOppgaveResponseDto
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/oppgave")
@ProtectedWithClaims(issuer = "azuread")
@Validated
class OppgaveController(val oppgaveService: OppgaveService) {

    @PostMapping(path = ["/hent-oppgaver"],
                 consumes = [MediaType.APPLICATION_JSON_VALUE],
                 produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentOppgaver(@RequestBody restFinnOppgaveRequest: RestFinnOppgaveRequest)
            : ResponseEntity<Ressurs<FinnOppgaveResponseDto>> =
            try {
                val oppgaver: FinnOppgaveResponseDto = oppgaveService.hentOppgaver(restFinnOppgaveRequest.tilFinnOppgaveRequest())
                ResponseEntity.ok().body(Ressurs.success(oppgaver, "Finn oppgaver OK"))
            } catch (e: Throwable) {
                illegalState("Henting av oppgaver feilet", e)
            }


//    @PostMapping(path = ["/{oppgaveId}/fordel"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun fordelOppgave(@PathVariable(name = "oppgaveId") oppgaveId: Long,
//                      @RequestParam("saksbehandler") saksbehandler: String
//    ): ResponseEntity<Ressurs<String>> {
//
//        Result.runCatching {
//            oppgaveService.fordelOppgave(oppgaveId, saksbehandler)
//        }.fold(
//                onSuccess = { return ResponseEntity.ok().body(Ressurs.success(it)) },
//                onFailure = { return illegalState("Feil ved tildeling av oppgave", it) }
//        )
//    }
//
//    @PostMapping(path = ["/{oppgaveId}/tilbakestill"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun tilbakestillFordelingPåOppgave(@PathVariable(name = "oppgaveId") oppgaveId: Long): ResponseEntity<Ressurs<String>> {
//        Result.runCatching {
//            oppgaveService.tilbakestillFordelingPåOppgave(oppgaveId)
//        }.fold(
//                onSuccess = { return ResponseEntity.ok().body(Ressurs.Companion.success(it)) },
//                onFailure = { return illegalState("Feil ved tilbakestilling av tildeling på oppgave", it) }
//        )
//    }

//    @GetMapping(path = ["/{oppgaveId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun hentDataForManuellJournalføring(@PathVariable(name = "oppgaveId") oppgaveId: Long)
//            : ResponseEntity<Ressurs<DataForManuellJournalføring>> {
//
//        return Result.runCatching {
//            val oppgave = oppgaveService.hentOppgave(oppgaveId)
//            val personIdent = if (oppgave.aktoerId == null) null else {
//                integrasjonClient.hentPersonIdent(oppgave.aktoerId) ?: error("Fant ikke personident for aktør id")
//            }
//            val fagsak = if (personIdent == null) null else fagsakService.hentRestFagsakForPerson(personIdent).data
//
//            Ressurs.success(DataForManuellJournalføring(
//                    oppgave = oppgave,
//                    journalpost = if (oppgave.journalpostId == null) null else integrasjonClient.hentJournalpost(oppgave.journalpostId!!).data
//                                                                               ?: error("Feil ved henting av journalpost, data finnes ikke på ressurs"),
//                    person = personIdent?.ident?.let { personopplysningerService.hentPersoninfoMedRelasjoner(it).toRestPersonInfo(it) },
//                    fagsak = fagsak
//            ))
//        }.fold(
//                onSuccess = { return ResponseEntity.ok().body(it) },
//                onFailure = { illegalState("Ukjent feil ved henting data for manuell journalføring.", it) }
//        )
//    }
}