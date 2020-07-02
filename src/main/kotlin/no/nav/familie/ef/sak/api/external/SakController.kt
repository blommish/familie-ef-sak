package no.nav.familie.ef.sak.api.external

import no.nav.familie.ef.sak.api.dto.SakDto
import no.nav.familie.ef.sak.service.SakService
import no.nav.familie.ef.sak.validering.SakstilgangConstraint
import no.nav.familie.kontrakter.ef.sak.SakRequest
import no.nav.familie.kontrakter.ef.søknad.*
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@RestController
@RequestMapping(path = ["/api/sak"])
@ProtectedWithClaims(issuer = "azuread")
@Validated
class SakController(private val sakService: SakService) {

    @PostMapping("sendInn")
    fun sendInn(@RequestBody sak: SakRequest): HttpStatus {
        sakService.mottaSak(sak)

        return HttpStatus.CREATED
    }

    @PostMapping("dummy")
    fun dummy(): HttpStatus {
        val sak = SakRequest(SøknadMedVedlegg(Testsøknad.søknad, emptyList()), "123", "321")
        sakService.mottaSak(sak)

        return HttpStatus.CREATED
    }

    @GetMapping("/{id}")
    fun dummy(@SakstilgangConstraint @PathVariable("id") id: UUID): Ressurs<SakDto> {
        return Ressurs.success(sakService.hentSakDto(id))
    }

}

internal object Testsøknad {

    val søknad = Søknad(Søknadsfelt("Søker", personalia()),
                        Søknadsfelt("innsending", Innsendingsdetaljer(Søknadsfelt("Dato mottatt", LocalDateTime.now()))),
                        Søknadsfelt("Detaljer om sivilstand", sivilstandsdetaljer()),
                        Søknadsfelt("Opphold i Norge", medlemskapsdetaljer()),
                        Søknadsfelt("Bosituasjonen din", bosituasjon()),
                        Søknadsfelt("Sivilstandsplaner", sivilstandsplaner()),
                        Søknadsfelt("Barn fra folkeregisteret", listOf(folkeregisterbarn())),
                        Søknadsfelt("Arbeid, utdanning og andre aktiviteter", aktivitet()),
                        Søknadsfelt("Mer om situasjonen din", situasjon()),
                        Søknadsfelt("Når søker du stønad fra?", stønadsstart()))

    val vedleggId = "d5531f89-0079-4715-a337-9fd28f811f2f"
    val vedlegg = listOf(Vedlegg(vedleggId, "vedlegg.pdf", "tittel", "filinnehold".toByteArray()))

    private fun stønadsstart() = Stønadsstart(Søknadsfelt("Fra måned", Month.AUGUST),
                                              Søknadsfelt("Fra år", 2018),
                                              Søknadsfelt("Søker fra bestemt månde", true))

    @Suppress("LongLine")
    private fun situasjon(): Situasjon {
        return Situasjon(Søknadsfelt("Gjelder noe av dette deg?",
                                     listOf("Barnet mitt er sykt",
                                            "Jeg har søkt om barnepass, men ikke fått plass enda",
                                            "Jeg har barn som har behov for særlig tilsyn på grunn av fysiske, psykiske eller store sosiale problemer")),
                         dokumentfelt("Legeerklæring"),
                         dokumentfelt("Legeattest for egen sykdom eller sykt barn"),
                         dokumentfelt("Avslag på søknad om barnehageplass, skolefritidsordning e.l."),
                         dokumentfelt("Dokumentasjon av særlig tilsynsbehov"),
                         dokumentfelt("Dokumentasjon av studieopptak"),
                         dokumentfelt("Læringskontrakt"),
                         Søknadsfelt("Når skal du starte i ny jobb?", LocalDate.of(2045, 12, 16)),
                         dokumentfelt("Dokumentasjon av jobbtilbud"),
                         Søknadsfelt("Når skal du starte utdanningen?", LocalDate.of(2025, 7, 28)),
                         Søknadsfelt("Har du sagt opp jobben eller redusert arbeidstiden de siste 6 månedene?",
                                     "Ja, jeg har sagt opp jobben eller tatt frivillig permisjon (ikke foreldrepermisjon)"),
                         Søknadsfelt("Hvorfor sa du opp?", "Sjefen var dum"),
                         Søknadsfelt("Når sa du opp?", LocalDate.of(2014, 1, 12)),
                         dokumentfelt("Dokumentasjon av arbeidsforhold"))
    }

    @Suppress("LongLine")
    private fun aktivitet(): Aktivitet {
        return Aktivitet(Søknadsfelt("Hvordan er arbeidssituasjonen din?",
                                     listOf("Jeg er hjemme med barn under 1 år (vises kun hvis har barn under 1 år)",
                                            "Jeg er i arbeid",
                                            "Jeg er selvstendig næringsdrivende eller frilanser")),
                         Søknadsfelt("Om arbeidsforholdet ditt",
                                     listOf(Arbeidsgiver(Søknadsfelt("Navn på arbeidsgiveren", "Palpatine"),
                                                         Søknadsfelt("Hvor mye jobber du?", 15),
                                                         Søknadsfelt("Er stillingen fast eller midlertidig?",
                                                                     "Fast"),
                                                         Søknadsfelt("Har du en sluttdato?", true),
                                                         Søknadsfelt("Når skal du slutte?",
                                                                     LocalDate.of(2020, 11, 18))))),
                         Søknadsfelt("Om firmaet du driver",
                                     Selvstendig(Søknadsfelt("Navn på firma", "Bobs burgers"),
                                                 Søknadsfelt("Organisasjonsnummer", "987654321"),
                                                 Søknadsfelt("Når etablerte du firmaet?",
                                                             LocalDate.of(2018, 4, 5)),
                                                 Søknadsfelt("Hvor mye jobber du?", 150),
                                                 Søknadsfelt("Hvordan ser arbeidsuken din ut?",
                                                             "Veldig tung"))),
                         Søknadsfelt("Om virksomheten du etablerer",
                                     Virksomhet(Søknadsfelt("Beskriv virksomheten",
                                                            "Den kommer til å revolusjonere verden"))),
                         Søknadsfelt("Når du er arbeidssøker",
                                     Arbeidssøker(Søknadsfelt("Er du registrert som arbeidssøker hos NAV?", true),
                                                  Søknadsfelt("Er du villig til å ta imot tilbud om arbeid eller arbeidsmarkedstiltak?",
                                                              true),
                                                  Søknadsfelt("Kan du begynne i arbeid senest én uke etter at du har fått tilbud om jobb?",
                                                              true),
                                                  Søknadsfelt("Har du eller kan du skaffe barnepass senest innen en uke etter at du har fått tilbud om jobb eller arbeidsmarkedstiltak?",
                                                              false),
                                                  Søknadsfelt("Hvor ønsker du å søke arbeid?",
                                                              "Kun i bodistriktet mitt, ikke mer enn 1 times reisevei"),
                                                  Søknadsfelt("Ønsker du å stå som arbeidssøker til minst 50% stilling?",
                                                              true))),
                         Søknadsfelt("Utdanningen du skal ta",
                                     UnderUtdanning(Søknadsfelt("Skole/utdanningssted", "UiO"),
                                                    Søknadsfelt("Utdanning",
                                                                Utdanning(Søknadsfelt("Linje/kurs/grad",
                                                                                      "Profesjonsstudium Informatikk"),
                                                                          Søknadsfelt("Når skal du være elev/student?",
                                                                                      Periode(Month.JANUARY,
                                                                                              1999,
                                                                                              Month.OCTOBER,
                                                                                              2004))
                                                                )),
                                                    Søknadsfelt("Er utdanningen offentlig eller privat?",
                                                                "Offentlig"),
                                                    Søknadsfelt("Heltid, eller deltid", "Deltid"),
                                                    Søknadsfelt("Hvor mye skal du studere?", 300),
                                                    Søknadsfelt("Hva er målet med utdanningen?",
                                                                "Økonomisk selvstendighet"),
                                                    Søknadsfelt("Har du tatt utdanning etter grunnskolen?", true),
                                                    Søknadsfelt("Tidligere Utdanning",
                                                                listOf(Utdanning(Søknadsfelt("Linje/kurs/grad",
                                                                                             "Master Fysikk"),
                                                                                 Søknadsfelt("Når var du elev/student?",
                                                                                             Periode(Month.JANUARY,
                                                                                                     1999,
                                                                                                     Month.OCTOBER,
                                                                                                     2004))
                                                                ))))))
    }

    @Suppress("LongLine")
    private fun folkeregisterbarn(): Barn {
        return Barn(Søknadsfelt("Navn", "Lykkeliten"),
                    Søknadsfelt("Fødselsnummer", Fødselsnummer("31081953069")),
                    Søknadsfelt("Har samme adresse som søker", true),
                    Søknadsfelt("Har ikke samme adresse som søker beskrivelse", "Dette er en beskrivelse."),
                    Søknadsfelt("Er barnet født?", false),
                    Søknadsfelt("Termindato", LocalDate.of(2020, 5, 16)),
                    dokumentfelt("Bekreftelse på ventet fødselsdato"),
                    Søknadsfelt("Barnets andre forelder",
                                AnnenForelder(person = Søknadsfelt("personalia", personMinimum()))),
                    Søknadsfelt("samvær",
                                Samvær(Søknadsfelt("Har du og den andre forelderen skriftlig avtale om delt bosted for barnet?",
                                                   true),
                                       dokumentfelt("Avtale om delt bosted for barna"),
                                       Søknadsfelt("Har den andre forelderen samvær med barnet",
                                                   "Ja, men ikke mer enn vanlig samværsrett"),
                                       Søknadsfelt("Har dere skriftlig samværsavtale for barnet?",
                                                   "Ja, men den beskriver ikke når barnet er sammen med hver av foreldrene"),
                                       dokumentfelt("Avtale om samvær"),
                                       dokumentfelt("Skal barnet bo hos deg"),
                                       Søknadsfelt("Hvordan praktiserer dere samværet?",
                                                   "Litt hver for oss"),
                                       Søknadsfelt("Bor du og den andre forelderen til [barnets navn] i samme hus/blokk, gårdstun, kvartal eller vei?",
                                                   "ja"),
                                       Søknadsfelt("Bor du og den andre forelderen til [barnets navn] i samme hus/blokk, gårdstun, kvartal eller vei?",
                                                   "Dette er en beskrivelse"),
                                       Søknadsfelt("Har du bodd sammen med den andre forelderen til [barnets fornavn] før?",
                                                   true),
                                       Søknadsfelt("Når flyttet dere fra hverandre?",
                                                   LocalDate.of(2018, 7, 21)),
                                       dokumentfelt("Erklæring om samlivsbrudd"),
                                       Søknadsfelt("Hvor mye er du sammen med den andre forelderen til barnet?",
                                                   "Vi møtes også uten at barnet er til stede"),
                                       Søknadsfelt("Beskriv  hvor mye er du sammen med den andre forelderen til barnet?",
                                                   "Vi sees stadig vekk"))))
    }

    private fun sivilstandsplaner(): Sivilstandsplaner {
        return Sivilstandsplaner(Søknadsfelt("Har du konkrete planer om å gifte deg eller bli samboer", true),
                                 Søknadsfelt("Når skal dette skje?", LocalDate.of(2021, 4, 15)),
                                 Søknadsfelt("Hvem skal du gifte deg eller bli samboer med?", personMinimum()))
    }

    private fun bosituasjon(): Bosituasjon {
        return Bosituasjon(Søknadsfelt("Deler du bolig med andre voksne?",
                                       "Ja, jeg har samboer og lever i et ekteskapslignende forhold"),
                           Søknadsfelt("Om samboeren din", personMinimum()),
                           Søknadsfelt("Når flyttet dere sammen?", LocalDate.of(2018, 8, 12)))
    }

    private fun medlemskapsdetaljer(): Medlemskapsdetaljer {
        return Medlemskapsdetaljer(Søknadsfelt("Oppholder du deg i Norge?", true),
                                   Søknadsfelt("Har du bodd i Norge de siste tre årene?", true),
                                   Søknadsfelt("",
                                               listOf(Utenlandsopphold(Søknadsfelt("Fra",
                                                                                   LocalDate.of(2012, 12, 4)),
                                                                       Søknadsfelt("Til",
                                                                                   LocalDate.of(2012, 12, 18)),
                                                                       Søknadsfelt("Hvorfor bodde du i utlandet?",
                                                                                   "Granca, Granca, Granca")))))
    }

    @Suppress("LongLine")
    private fun sivilstandsdetaljer(): Sivilstandsdetaljer {
        return Sivilstandsdetaljer(Søknadsfelt("Er du gift uten at dette er formelt registrert eller godkjent i Norge?",
                                               true),
                                   dokumentfelt("giftIUtlandetDokumentasjon"),
                                   Søknadsfelt("Er du separert eller skilt uten at dette er formelt registrert eller godkjent i Norge?",
                                               true),
                                   dokumentfelt("separertEllerSkiltIUtlandetDokumentasjon"),
                                   Søknadsfelt("Har dere søkt om separasjon, søkt om skilsmisse eller reist sak for domstolen?",
                                               true),
                                   Søknadsfelt("Når søkte dere eller reiste sak?", LocalDate.of(2015, 12, 23)),
                                   dokumentfelt("Skilsmisse- eller separasjonsbevilling"),
                                   Søknadsfelt("Hva er grunnen til at du er alene med barn?",
                                               "Trives best alene"),
                                   dokumentfelt("Erklæring om samlivsbrudd"),
                                   Søknadsfelt("Dato for samlivsbrudd", LocalDate.of(2014, 10, 3)),
                                   Søknadsfelt("Når flyttet dere fra hverandre?", LocalDate.of(2014, 10, 4)),
                                   Søknadsfelt("Når skjedde endringen / når skal endringen skje?",
                                               LocalDate.of(2013, 4, 17)))
    }

    private fun personalia(): Personalia {
        return Personalia(Søknadsfelt("Fødselsnummer", Fødselsnummer("24117938529")),
                          Søknadsfelt("Navn", "Kari Nordmann"),
                          Søknadsfelt("Statsborgerskap", "Norsk"),
                          adresseSøknadsfelt(),
                          Søknadsfelt("Telefonnummer", "12345678"),
                          Søknadsfelt("Sivilstand", "Ugift"))
    }

    private fun adresseSøknadsfelt(): Søknadsfelt<Adresse> {
        return Søknadsfelt("Adresse",
                           Adresse("Jerpefaret 5C",
                                   "1440",
                                   "Drøbak",
                                   "Norge"))
    }

    private fun dokumentfelt(tittel: String) =
            Søknadsfelt(tittel, Dokumentasjon(Søknadsfelt("Har allerede sendt inn", false), listOf(Dokument(vedleggId, tittel))))

    private fun personMinimum(): PersonMinimum {
        return PersonMinimum(Søknadsfelt("Navn", "Bob Burger"),
                             null,
                             Søknadsfelt("Fødselsdato", LocalDate.of(1992, 2, 18)))
    }
}
