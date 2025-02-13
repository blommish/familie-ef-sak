package no.nav.familie.ef.sak.integration.dto.pdl

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

data class PdlResponse<T>(val data: T,
                          val errors: List<PdlError>?) {

    fun harFeil(): Boolean {
        return errors != null && errors.isNotEmpty()
    }

    fun errorMessages(): String {
        return errors?.joinToString { it -> it.message } ?: ""
    }
}

data class PdlBolkResponse<T>(val data: PersonBolk<T>?, val errors: List<PdlError>?) {

    fun errorMessages(): String {
        return errors?.joinToString { it -> it.message } ?: ""
    }
}

data class PdlError(val message: String,
                    val extensions: PdlExtensions?)

data class PdlExtensions(val code: String?) {

    fun notFound() = code == "not_found"
}

data class PdlSøkerData(val person: PdlSøker?)

data class PersonDataBolk<T>(val ident: String, val code: String, val person: T?)
data class PersonBolk<T>(val personBolk: List<PersonDataBolk<T>>)

interface PdlPerson {

    val fødsel: List<Fødsel>
    val bostedsadresse: List<Bostedsadresse>
}

data class PdlIdent(val ident: String, val historisk: Boolean)

data class PdlIdenter(val identer: List<PdlIdent>)

data class PdlHentIdenter(val hentIdenter: PdlIdenter?)

data class PdlPersonKort(val navn: List<Navn>)

data class PdlSøkerKort(@JsonProperty("kjoenn") val kjønn: List<Kjønn>,
                        val navn: List<Navn>)

data class PdlSøker(val adressebeskyttelse: List<Adressebeskyttelse>,
                    override val bostedsadresse: List<Bostedsadresse>,
                    @JsonProperty("doedsfall") val dødsfall: List<Dødsfall>,
                    val forelderBarnRelasjon: List<ForelderBarnRelasjon>,
                    @JsonProperty("foedsel") override val fødsel: List<Fødsel>,
                    val folkeregisterpersonstatus: List<Folkeregisterpersonstatus>,
                    val fullmakt: List<Fullmakt>,
                    @JsonProperty("kjoenn") val kjønn: List<Kjønn>,
                    val kontaktadresse: List<Kontaktadresse>,
                    val navn: List<Navn>,
                    val opphold: List<Opphold>,
                    val oppholdsadresse: List<Oppholdsadresse>,
                    val sivilstand: List<Sivilstand>,
                    val statsborgerskap: List<Statsborgerskap>,
                    val telefonnummer: List<Telefonnummer>,
                    val tilrettelagtKommunikasjon: List<TilrettelagtKommunikasjon>,
                    val innflyttingTilNorge: List<InnflyttingTilNorge>,
                    val utflyttingFraNorge: List<UtflyttingFraNorge>,
                    val vergemaalEllerFremtidsfullmakt: List<VergemaalEllerFremtidsfullmakt>) : PdlPerson

data class PdlBarn(val adressebeskyttelse: List<Adressebeskyttelse>,
                   override val bostedsadresse: List<Bostedsadresse>,
                   val deltBosted: List<DeltBosted>,
                   @JsonProperty("doedsfall") val dødsfall: List<Dødsfall>,
                   val forelderBarnRelasjon: List<ForelderBarnRelasjon>,
                   @JsonProperty("foedsel") override val fødsel: List<Fødsel>,
                   val navn: List<Navn>) : PdlPerson

data class PdlAnnenForelder(val adressebeskyttelse: List<Adressebeskyttelse>,
                            override val bostedsadresse: List<Bostedsadresse>,
                            @JsonProperty("doedsfall") val dødsfall: List<Dødsfall>,
                            @JsonProperty("foedsel") override val fødsel: List<Fødsel>,
                            val navn: List<Navn>,
                            val opphold: List<Opphold>,
                            val oppholdsadresse: List<Oppholdsadresse>,
                            val statsborgerskap: List<Statsborgerskap>,
                            val innflyttingTilNorge: List<InnflyttingTilNorge>,
                            val utflyttingFraNorge: List<UtflyttingFraNorge>) : PdlPerson

data class Metadata(val historisk: Boolean)

data class DeltBosted(val startdatoForKontrakt: LocalDate,
                      val sluttdatoForKontrakt: LocalDate?,
                      val vegadresse: Vegadresse?,
                      val ukjentBosted: UkjentBosted?,
                      val metadata: Metadata
)

data class Folkeregistermetadata(val gyldighetstidspunkt: LocalDateTime?,
                                 @JsonProperty("opphoerstidspunkt") val opphørstidspunkt: LocalDateTime?)

data class Bostedsadresse(val angittFlyttedato: LocalDate?,
                          val coAdressenavn: String?,
                          val folkeregistermetadata: Folkeregistermetadata,
                          val utenlandskAdresse: UtenlandskAdresse?,
                          val vegadresse: Vegadresse?,
                          val ukjentBosted: UkjentBosted?,
                          val matrikkeladresse: Matrikkeladresse?,
                          val metadata: Metadata) {

    fun matrikkelId(): Long? {
        return matrikkeladresse?.matrikkelId ?: vegadresse?.matrikkelId
    }
}

data class Oppholdsadresse(val gyldigFraOgMed: LocalDate?,
                           val gyldigTilOgMed: LocalDate? = null,
                           val coAdressenavn: String?,
                           val utenlandskAdresse: UtenlandskAdresse?,
                           val vegadresse: Vegadresse?,
                           val oppholdAnnetSted: String?,
                           val metadata: Metadata)

data class Kontaktadresse(val coAdressenavn: String?,
                          val gyldigFraOgMed: LocalDate?,
                          val gyldigTilOgMed: LocalDate?,
                          val postadresseIFrittFormat: PostadresseIFrittFormat?,
                          val postboksadresse: Postboksadresse?,
                          val type: KontaktadresseType,
                          val utenlandskAdresse: UtenlandskAdresse?,
                          val utenlandskAdresseIFrittFormat: UtenlandskAdresseIFrittFormat?,
                          val vegadresse: Vegadresse?)

@Suppress("unused")
enum class KontaktadresseType {

    @JsonProperty("Innland") INNLAND,
    @JsonProperty("Utland") UTLAND
}

data class Postboksadresse(val postboks: String,
                           val postbokseier: String?,
                           val postnummer: String?)

data class PostadresseIFrittFormat(val adresselinje1: String?,
                                   val adresselinje2: String?,
                                   val adresselinje3: String?,
                                   val postnummer: String?)

data class Vegadresse(val husnummer: String?,
                      val husbokstav: String?,
                      val bruksenhetsnummer: String?,
                      val adressenavn: String?,
                      val kommunenummer: String?,
                      val tilleggsnavn: String?,
                      val postnummer: String?,
                      val koordinater: Koordinater?,
                      val matrikkelId: Long?)

data class UkjentBosted(val bostedskommune: String?)

data class Koordinater(val x: Float?,
                       val y: Float?,
                       val z: Float?,
                       val kvalitet: Int?)

data class Adressebeskyttelse(val gradering: AdressebeskyttelseGradering, val metadata: Metadata)

enum class AdressebeskyttelseGradering {
    STRENGT_FORTROLIG,
    STRENGT_FORTROLIG_UTLAND,
    FORTROLIG,
    UGRADERT
}

data class Fødsel(@JsonProperty("foedselsaar") val fødselsår: Int?,
                  @JsonProperty("foedselsdato") val fødselsdato: LocalDate?,
                  @JsonProperty("foedeland") val fødeland: String?,
                  @JsonProperty("foedested") val fødested: String?,
                  @JsonProperty("foedekommune") val fødekommune: String?,
                  val metadata: Metadata)

data class Dødsfall(@JsonProperty("doedsdato") val dødsdato: LocalDate?)

data class ForelderBarnRelasjon(val relatertPersonsIdent: String,
                                val relatertPersonsRolle: Familierelasjonsrolle,
                                val minRolleForPerson: Familierelasjonsrolle?)

enum class Familierelasjonsrolle {
    BARN,
    MOR,
    FAR,
    MEDMOR
}

data class Folkeregisterpersonstatus(val status: String,
                                     val forenkletStatus: String,
                                     val metadata: Metadata)

data class Fullmakt(val gyldigFraOgMed: LocalDate,
                    val gyldigTilOgMed: LocalDate,
                    val motpartsPersonident: String,
                    val motpartsRolle: MotpartsRolle,
                    val omraader: List<String>)

enum class MotpartsRolle {
    FULLMAKTSGIVER,
    FULLMEKTIG
}

data class Kjønn(@JsonProperty("kjoenn") val kjønn: KjønnType)

enum class KjønnType {
    KVINNE,
    MANN,
    UKJENT
}

data class Navn(val fornavn: String,
                val mellomnavn: String?,
                val etternavn: String,
                val metadata: Metadata)

data class Personnavn(val etternavn: String,
                      val fornavn: String,
                      val mellomnavn: String?)


data class Telefonnummer(val landskode: String,
                         val nummer: String,
                         val prioritet: Int)

data class TilrettelagtKommunikasjon(@JsonProperty("talespraaktolk") val talespråktolk: Tolk?,
                                     @JsonProperty("tegnspraaktolk") val tegnspråktolk: Tolk?)

data class Tolk(@JsonProperty("spraak") val språk: String?)

data class Statsborgerskap(val land: String,
                           val gyldigFraOgMed: LocalDate?,
                           val gyldigTilOgMed: LocalDate?)

data class Opphold(val type: Oppholdstillatelse,
                   val oppholdFra: LocalDate?,
                   val oppholdTil: LocalDate?)

enum class Oppholdstillatelse {
    MIDLERTIDIG,
    PERMANENT,
    OPPLYSNING_MANGLER
}

data class Sivilstand(val type: Sivilstandstype,
                      val gyldigFraOgMed: LocalDate?,
                      val relatertVedSivilstand: String?,
                      val bekreftelsesdato: String?,
                      val metadata: Metadata)

enum class Sivilstandstype {
    UOPPGITT,
    UGIFT,
    GIFT,
    ENKE_ELLER_ENKEMANN,
    SKILT,
    SEPARERT,
    REGISTRERT_PARTNER,
    SEPARERT_PARTNER,
    SKILT_PARTNER,
    GJENLEVENDE_PARTNER
}

data class InnflyttingTilNorge(val fraflyttingsland: String?,
                               val fraflyttingsstedIUtlandet: String?,
                               val folkeregistermetadata: Folkeregistermetadata)

data class UtflyttingFraNorge(val tilflyttingsland: String?,
                              val tilflyttingsstedIUtlandet: String?,
                              val folkeregistermetadata: Folkeregistermetadata)

data class UtenlandskAdresse(val adressenavnNummer: String?,
                             val bySted: String?,
                             val bygningEtasjeLeilighet: String?,
                             val landkode: String,
                             val postboksNummerNavn: String?,
                             val postkode: String?,
                             val regionDistriktOmraade: String?)

data class Matrikkeladresse(val matrikkelId: Long?,
                            val bruksenhetsnummer: String?
)

data class UtenlandskAdresseIFrittFormat(val adresselinje1: String?,
                                         val adresselinje2: String?,
                                         val adresselinje3: String?,
                                         val byEllerStedsnavn: String?,
                                         val landkode: String,
                                         val postkode: String?)

data class VergeEllerFullmektig(val motpartsPersonident: String?,
                                val navn: Personnavn?,
                                val omfang: String?,
                                val omfangetErInnenPersonligOmraade: Boolean)

data class VergemaalEllerFremtidsfullmakt(val embete: String?,
                                          val folkeregistermetadata: Folkeregistermetadata?,
                                          val type: String?,
                                          val vergeEllerFullmektig: VergeEllerFullmektig)
