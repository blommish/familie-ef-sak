package no.nav.familie.ef.sak.service.steg

import com.fasterxml.jackson.module.kotlin.readValue
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.familie.ef.sak.api.dto.BeslutteVedtakDto
import no.nav.familie.ef.sak.api.dto.TotrinnskontrollDto
import no.nav.familie.ef.sak.repository.VedtaksbrevRepository
import no.nav.familie.ef.sak.repository.domain.*
import no.nav.familie.ef.sak.service.FagsakService
import no.nav.familie.ef.sak.service.OppgaveService
import no.nav.familie.ef.sak.service.TotrinnskontrollService
import no.nav.familie.ef.sak.task.IverksettMotOppdragTask
import no.nav.familie.ef.sak.task.OpprettOppgaveTask
import no.nav.familie.kontrakter.felles.objectMapper
import no.nav.familie.kontrakter.felles.oppgave.Oppgavetype
import no.nav.familie.prosessering.domene.Task
import no.nav.familie.prosessering.domene.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class BeslutteVedtakStegTest {


    private val taskRepository = mockk<TaskRepository>()
    private val fagsakService = mockk<FagsakService>()
    private val totrinnskontrollService = mockk<TotrinnskontrollService>(relaxed = true)
    private val oppgaveService = mockk<OppgaveService>()
    private val vedtaksbrevRepository = mockk<VedtaksbrevRepository>()

    private val beslutteVedtakSteg = BeslutteVedtakSteg(taskRepository, fagsakService, oppgaveService, totrinnskontrollService, vedtaksbrevRepository)
    private val fagsak = Fagsak(stønadstype = Stønadstype.OVERGANGSSTØNAD,
                                søkerIdenter = setOf(FagsakPerson(ident = "12345678901")))

    private lateinit var taskSlot: CapturingSlot<Task>

    @BeforeEach
    internal fun setUp() {
        taskSlot = slot()
        every {
            fagsakService.hentFagsak(any())
        } returns fagsak
        every {
            taskRepository.save(capture(taskSlot))
        } returns Task("", "", Properties())
        every { oppgaveService.hentOppgaveSomIkkeErFerdigstilt(any(), any()) } returns mockk()
    }

    @Test
    internal fun `skal opprette iverksettMotOppdragTask etter beslutte vedtak hvis godkjent`() {
        val nesteSteg = utførTotrinnskontroll(godkjent = true)

        assertThat(nesteSteg).isEqualTo(StegType.IVERKSETT_MOT_OPPDRAG)
        assertThat(taskSlot.captured.type).isEqualTo(IverksettMotOppdragTask.TYPE)
    }

    @Test
    internal fun `skal opprette opprettBehandleUnderkjentVedtakOppgave etter beslutte vedtak hvis underkjent`() {
        val nesteSteg = utførTotrinnskontroll(godkjent = false)

        val deserializedPayload = objectMapper.readValue<OpprettOppgaveTask.OpprettOppgaveTaskData>(taskSlot.captured.payload)

        assertThat(nesteSteg).isEqualTo(StegType.SEND_TIL_BESLUTTER)
        assertThat(taskSlot.captured.type).isEqualTo(OpprettOppgaveTask.TYPE)
        assertThat(deserializedPayload.oppgavetype).isEqualTo(Oppgavetype.BehandleUnderkjentVedtak)
    }

    private fun utførTotrinnskontroll(godkjent: Boolean): StegType {
        val nesteSteg = beslutteVedtakSteg.utførOgReturnerNesteSteg(Behandling(fagsakId = fagsak.id,
                                                                               type = BehandlingType.FØRSTEGANGSBEHANDLING,
                                                                               status = BehandlingStatus.FATTER_VEDTAK,
                                                                               steg = beslutteVedtakSteg.stegType(),
                                                                               resultat = BehandlingResultat.IKKE_SATT),
                                                                    BeslutteVedtakDto(godkjent = godkjent))
        return nesteSteg
    }
}