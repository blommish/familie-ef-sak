package no.nav.familie.ef.sak.api.dto

import no.nav.familie.ef.sak.mapper.OvergangsstønadMapper
import no.nav.familie.ef.sak.repository.domain.søknad.SøknadsskjemaOvergangsstønad
import java.time.LocalDate
import java.time.YearMonth

data class OvergangsstønadDto(val aktivitet: AktivitetDto,
                              val sagtOppEllerRedusertStilling: SagtOppEllerRedusertStillingDto?,
                              val stønadsperiode: StønadsperiodeDto)

data class AktivitetDto(val arbeidssituasjon: List<String>,
                        val arbeidsforhold: List<ArbeidsforholdDto>,
                        val selvstendig: List<SelvstendigDto>,
                        val aksjeselskap: List<AksjeselskapDto>,
                        val arbeidssøker: ArbeidssøkerDto?,
                        val underUtdanning: UnderUtdanningDto?,
                        val aktivitetsplikt: String?,
                        val situasjon: SituasjonDto,
                        val virksomhet: VirksomhetDto?)

data class SagtOppEllerRedusertStillingDto(val sagtOppEllerRedusertStilling: String?,
                                           val årsak: String?,
                                           val dato: LocalDate?,
                                           val dokumentasjon: DokumentasjonDto?)

data class ArbeidsforholdDto(val arbeidsgivernavn: String,
                             val arbeidsmengde: Int?,
                             val fastEllerMidlertidig: String,
                             val sluttdato: LocalDate?)

data class SelvstendigDto(val firmanavn: String,
                          val organisasjonsnummer: String,
                          val etableringsdato: LocalDate,
                          val arbeidsmengde: Int?,
                          val hvordanSerArbeidsukenUt: String)

data class AksjeselskapDto(val navn: String,
                           val arbeidsmengde: Int?)

data class VirksomhetDto(val virksomhetsbeskrivelse: String, val dokumentasjon: DokumentasjonDto?)

data class ArbeidssøkerDto(val registrertSomArbeidssøkerNav: Boolean,
                           val villigTilÅTaImotTilbudOmArbeid: Boolean,
                           val kanDuBegynneInnenEnUke: Boolean,
                           val kanDuSkaffeBarnepassInnenEnUke: Boolean?,
                           val hvorØnskerDuArbeid: String,
                           val ønskerDuMinst50ProsentStilling: Boolean,
                           val ikkeVilligTilÅTaImotTilbudOmArbeidDokumentasjon: DokumentasjonDto?)

data class UnderUtdanningDto(val skoleUtdanningssted: String,
                             val utdanning: UtdanningDto?,
                             val offentligEllerPrivat: String,
                             val heltidEllerDeltid: String,
                             val hvorMyeSkalDuStudere: Int?,
                             val hvaErMåletMedUtdanningen: String?,
                             val utdanningEtterGrunnskolen: Boolean,
                             val tidligereUtdanninger: List<UtdanningDto>)

data class UtdanningDto(val linjeKursGrad: String,
                        val nårVarSkalDuVæreElevStudent: PeriodeDto)

data class SituasjonDto(val sykdom: DokumentasjonDto?,
                        val barnsSykdom: DokumentasjonDto?,
                        val manglendeBarnepass: DokumentasjonDto?,
                        val barnMedSærligeBehov: DokumentasjonDto?)

data class StønadsperiodeDto(val resterendeAvHovedperiode: String,
                             val søkerStønadFra: YearMonth?)
