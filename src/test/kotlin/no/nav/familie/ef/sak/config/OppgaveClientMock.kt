package no.nav.familie.ef.sak.no.nav.familie.ef.sak.config

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import no.nav.familie.ef.sak.integration.OppgaveClient
import no.nav.familie.kontrakter.felles.oppgave.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.LocalDate


@Configuration
class OppgaveClientMock {

    @Bean
    @Primary
    fun oppgaveClient(): OppgaveClient {
        val oppgaveClient: OppgaveClient = mockk()

        every {
            oppgaveClient.hentOppgaver(any())
        } returns FinnOppgaveResponseDto(3, listOf(oppgave1, oppgave2, oppgave3))

        every { oppgaveClient.finnOppgaveMedId(11) } returns oppgaveUtenJournalpost
        every { oppgaveClient.finnOppgaveMedId(1) } returns oppgave1
        every { oppgaveClient.finnOppgaveMedId(2) } returns oppgave2
        every { oppgaveClient.finnOppgaveMedId(3) } returns oppgave3

        every { oppgaveClient.opprettOppgave(any()) } returns 2L

        every { oppgaveClient.fordelOppgave(any(), any()) } returns 12345678L

        every { oppgaveClient.ferdigstillOppgave(any()) } just Runs

        return oppgaveClient
    }

    private val oppgave1 = lagOppgave(1L, Oppgavetype.Journalføring, "Z999999")
    private val oppgave2 = lagOppgave(2L, Oppgavetype.BehandleSak, "Z999999")
    private val oppgave3 = lagOppgave(3L, Oppgavetype.Journalføring, beskivelse = "")
    private val oppgaveUtenJournalpost = lagOppgave(4L, Oppgavetype.Journalføring, journalpostId = null)


    private fun lagOppgave(oppgaveId: Long,
                           oppgavetype: Oppgavetype,
                           tildeltRessurs: String? = null,
                           beskivelse: String? = "Beskrivelse av oppgaven. " +
                                                 "Denne teksten kan jo være lang, kort eller ikke inneholde noenting. ",
                            journalpostId: String? = "1234")
            : Oppgave {
        return Oppgave(id = oppgaveId,
                       aktoerId = "1234",
                       identer = listOf(OppgaveIdentV2("11111111111", IdentGruppe.FOLKEREGISTERIDENT)),
                       journalpostId = journalpostId,
                       tildeltEnhetsnr = "4408",
                       tilordnetRessurs = tildeltRessurs,
                       mappeId = 100000035,
                       behandlesAvApplikasjon = "FS22",
                       beskrivelse = beskivelse,
                       tema = Tema.ENF,
                       behandlingstema = "ab0071",
                       oppgavetype = oppgavetype.value,
                       opprettetTidspunkt = LocalDate.of(2020, 1, 1).toString(),
                       fristFerdigstillelse = LocalDate.of(2020, 2, 1).toString(),
                       prioritet = OppgavePrioritet.NORM,
                       status = StatusEnum.OPPRETTET
        )
    }
}