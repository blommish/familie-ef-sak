package no.nav.familie.ef.sak.api.oppgave

import no.nav.familie.kontrakter.felles.oppgave.Behandlingstema
import no.nav.familie.kontrakter.felles.oppgave.FinnOppgaveRequest
import no.nav.familie.kontrakter.felles.oppgave.Oppgavetype
import no.nav.familie.kontrakter.felles.oppgave.Tema
import java.time.LocalDate
import java.time.LocalDateTime

data class FinnOppgaveRequestDto(
        val behandlingstema: Behandlingstema? = null,
        val oppgavetype: Oppgavetype? = null,
        val enhet: String? = null,
        val saksbehandler: String? = null,
        val journalpostId: String? = null,
        val tilordnetRessurs: String? = null,
        val tildeltRessurs: Boolean? = null,
        val opprettetFomTidspunkt: LocalDateTime? = null,
        val opprettetTomTidspunkt: LocalDateTime? = null,
        val fristFomDato: LocalDate? = null,
        val fristTomDato: LocalDate? = null,
        val aktivFomDato: LocalDate? = null,
        val aktivTomDato: LocalDate? = null,
        val limit: Long? = null,
        val offset: Long? = null) {

    fun tilFinnOppgaveRequest(): FinnOppgaveRequest = FinnOppgaveRequest(
            tema = Tema.ENF,
            behandlingstema = this.behandlingstema,
            oppgavetype = this.oppgavetype,
            enhet = this.enhet,
            saksbehandler = this.saksbehandler,
            journalpostId = this.journalpostId,
            tildeltRessurs = this.tildeltRessurs,
            tilordnetRessurs = this.tilordnetRessurs,
            opprettetFomTidspunkt = this.opprettetFomTidspunkt,
            opprettetTomTidspunkt = this.opprettetTomTidspunkt,
            fristFomDato = this.fristFomDato,
            fristTomDato = this.fristTomDato,
            aktivFomDato = this.aktivFomDato,
            aktivTomDato = this.aktivTomDato,
            limit = this.limit,
            offset = this.offset)
}