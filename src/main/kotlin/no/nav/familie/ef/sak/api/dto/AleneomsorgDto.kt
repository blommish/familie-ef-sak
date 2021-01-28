package no.nav.familie.ef.sak.api.dto

import java.time.LocalDate

data class AleneomsorgDto(
    val barneId: String,
    val søknadsgrunnlag: AleneomsorgSøknadsgrunnlagDto,
    val registergrunnlagDto: AleneomsorgRegistergrunnlagDto
)


data class AleneomsorgSøknadsgrunnlagDto(
    val navn: String?,
    val fødselsnummer: String?,
    val fødselTermindato: LocalDate?,
    val skalBoBorHosSøker: Boolean?,
    val forelder: AnnenForelder?,
    val ikkeOppgittAnnenForelderBegrunnelse: String?,
    val spørsmålAvtaleOmDeltBosted: Boolean?,
    val skalAnnenForelderHaSamvær: String?,
    val harDereSkriftligAvtaleOmSamvær: String?,
    val hvordanPraktiseresSamværet: String?,
    val borAnnenForelderISammeHus: String?,
    val borAnnenForelderISammeHusBeskrivelse: String?,
    val harDereTidligereBoddSammen: Boolean?,
    val nårFlyttetDereFraHverandre: LocalDate?,
    val hvorMyeErDuSammenMedAnnenForelder: String?,
    val beskrivSamværUtenBarn: String?
)

data class AleneomsorgRegistergrunnlagDto(
    val navn: String,
    val fødselsnummer: String?,
    val skalBoBorHosSøker: Boolean?,
    val forelder: AnnenForelder?,
    )

data class AnnenForelder(
    val navn: String,
    val fødselsnummer: String?,
    val fødselsdato: LocalDate?,
    val bosattINorge: Boolean,
    val land: String?,
)