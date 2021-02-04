package no.nav.familie.ef.sak.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.familie.ef.sak.exception.PdlNotFoundException
import no.nav.familie.ef.sak.integration.InfotrygdReplikaClient
import no.nav.familie.ef.sak.integration.PdlClient
import no.nav.familie.ef.sak.integration.dto.pdl.PdlIdent
import no.nav.familie.ef.sak.integration.dto.pdl.PdlIdenter
import no.nav.familie.kontrakter.ef.infotrygd.InfotrygdPeriodeOvergangsstønad
import no.nav.familie.kontrakter.ef.infotrygd.InfotrygdPerioderOvergangsstønadRequest
import no.nav.familie.kontrakter.ef.infotrygd.InfotrygdPerioderOvergangsstønadResponse
import no.nav.familie.kontrakter.felles.ef.PerioderOvergangsstønadRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDate.parse

internal class PerioderOvergangsstønadServiceTest {

    private val pdlClient = mockk<PdlClient>()
    private val infotrygdReplikaClient = mockk<InfotrygdReplikaClient>(relaxed = true)
    private val perioderOvergangsstønadService = PerioderOvergangsstønadService(infotrygdReplikaClient, pdlClient)

    private val ident = "01234567890"

    @Test
    internal fun `hentPerioder henter perioder fra infotrygd med alle identer fra pdl`() {
        val historiskIdent = "01234567890"
        val fomDato = LocalDate.MIN
        val tomDato = LocalDate.MAX
        val request = PerioderOvergangsstønadRequest(ident, fomDato, tomDato)

        mockPdl(historiskIdent)
        every { infotrygdReplikaClient.hentPerioderOvergangsstønad(any()) } returns
                infotrygdResponse(InfotrygdPeriodeOvergangsstønad(ident, LocalDate.now(), LocalDate.now(), 10f))

        val hentPerioder = perioderOvergangsstønadService.hentPerioder(request)

        assertThat(hentPerioder.perioder).hasSize(1)

        verify(exactly = 1) { pdlClient.hentPersonidenter(ident, true) }
        verify(exactly = 1) {
            val infotrygdRequest = InfotrygdPerioderOvergangsstønadRequest(setOf(ident, historiskIdent), fomDato, tomDato)
            infotrygdReplikaClient.hentPerioderOvergangsstønad(infotrygdRequest)
        }
    }

    @Test
    internal fun `skal kalle infotrygd hvis pdl ikke finner personIdent med personIdent i requesten`() {
        every { pdlClient.hentPersonidenter(any(), true) } throws PdlNotFoundException()

        perioderOvergangsstønadService.hentPerioder(PerioderOvergangsstønadRequest(ident))
        verify(exactly = 1) {
            infotrygdReplikaClient.hentPerioderOvergangsstønad(InfotrygdPerioderOvergangsstønadRequest(setOf(ident)))
        }
    }

    @Test
    internal fun `skal slå sammen perioder til arena`() {
        mockPdl()
        val infotrygPerioder = listOf(periode(parse("2017-08-01"), parse("2018-04-30"), 1f, parse("2018-07-31")),
                                      periode(parse("2018-05-01"), parse("2020-07-31"), 1f, parse("2018-07-31")),
                                      periode(parse("2018-09-01"), parse("2018-12-31"), 1f),
                                      periode(parse("2019-01-01"), parse("2019-04-30"), 1f),
                                      periode(parse("2019-05-01"), parse("2020-04-30"), 1f),
                                      periode(parse("2020-05-01"), parse("2020-08-31"), 1f))

        every { infotrygdReplikaClient.hentPerioderOvergangsstønad(any()) } returns
                InfotrygdPerioderOvergangsstønadResponse(infotrygPerioder)

        val perioder = perioderOvergangsstønadService.hentPerioder(PerioderOvergangsstønadRequest(ident))

        val fomTomDatoer = perioder.perioder.map { it.fomDato to it.tomDato }
        assertThat(fomTomDatoer).isEqualTo(listOf(parse("2017-08-01") to parse("2018-07-31"),
                                                  parse("2018-09-01") to parse("2020-08-31")))
    }

    @Test
    internal fun `skal slå sammen perioder til arena 2`() {
        mockPdl()
        val infotrygPerioder = listOf(periode(parse("2018-12-01"), parse("2019-04-30"), 1f),
                                       periode(parse("2019-05-01"), parse("2019-12-31"), 1f),
                                       periode(parse("2019-12-01"), parse("2020-02-29"), 1f),
                                       periode(parse("2020-02-01"), parse("2020-04-30"), 1f),
                                       periode(parse("2020-05-01"), parse("2020-10-31"), 1f),
                                       periode(parse("2020-09-01"), parse("2020-12-31"), 1f),
                                       periode(parse("2021-01-01"), parse("2022-01-31"), 1f))

        every { infotrygdReplikaClient.hentPerioderOvergangsstønad(any()) } returns
                InfotrygdPerioderOvergangsstønadResponse(infotrygPerioder)

        val perioder = perioderOvergangsstønadService.hentPerioder(PerioderOvergangsstønadRequest(ident))

        val fomTomDatoer = perioder.perioder.map { it.fomDato to it.tomDato }
        assertThat(fomTomDatoer).isEqualTo(listOf(parse("2018-12-01") to parse("2022-01-31")))
    }

    private fun periode(fomDato: LocalDate, tomDato: LocalDate, beløp: Float, opphørsdato: LocalDate? = null) =
            InfotrygdPeriodeOvergangsstønad(ident, fomDato, tomDato, beløp, opphørsdato)

    private fun mockPdl(historiskIdent: String? = null) {
        val pdlIdenter = mutableListOf(PdlIdent(ident, false))
        if (historiskIdent != null) {
            pdlIdenter.add(PdlIdent(historiskIdent, true))
        }
        every { pdlClient.hentPersonidenter(ident, true) } returns PdlIdenter(pdlIdenter)
    }

    private fun infotrygdResponse(vararg infotrygdPeriodeOvergangsstønad: InfotrygdPeriodeOvergangsstønad) =
            InfotrygdPerioderOvergangsstønadResponse(infotrygdPeriodeOvergangsstønad.toList())
}