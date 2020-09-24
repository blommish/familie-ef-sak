package no.nav.familie.ef.sak.api.oppgave

import no.nav.familie.kontrakter.felles.oppgave.Behandlingstema
import no.nav.familie.kontrakter.felles.oppgave.FinnOppgaveRequest
import no.nav.familie.kontrakter.felles.oppgave.Oppgavetype
import no.nav.familie.kontrakter.felles.oppgave.Tema
import java.time.LocalDate

data class FinnOppgaveRequestDto(
        val behandlingstema: Behandlingstema? = null,
        val oppgavetype: Oppgavetype? = null,
        val enhet: String? = null,
        val saksbehandler: String? = null,
        val journalpostId: String? = null,
        val tilordnetRessurs: String? = null,
        val tildeltRessurs: Boolean? = null,
        val opprettetFom: LocalDate? = null,
        val opprettetTom: LocalDate? = null,
        val fristFom: LocalDate? = null,
        val fristTom: LocalDate? = null) {

    fun tilFinnOppgaveRequest(): FinnOppgaveRequest = FinnOppgaveRequest(
            tema = Tema.ENF,
            behandlingstema = this.behandlingstema,
            oppgavetype = this.oppgavetype,
            enhet = this.enhet,
            saksbehandler = this.saksbehandler,
            journalpostId = this.journalpostId,
            tildeltRessurs = this.tildeltRessurs,
            tilordnetRessurs = this.tilordnetRessurs,
            opprettetFomTidspunkt = this.opprettetFom?.atStartOfDay(),
            opprettetTomTidspunkt = this.opprettetTom?.plusDays(1)?.atStartOfDay(),
            fristFomDato = this.fristFom,
            fristTomDato = this.fristTom,
            aktivFomDato = null,
            aktivTomDato = null,
            limit = 150,
            offset = 0)
}