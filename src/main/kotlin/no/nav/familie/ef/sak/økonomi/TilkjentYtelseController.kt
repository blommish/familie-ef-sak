package no.nav.familie.ef.sak.økonomi

import no.nav.familie.ef.sak.økonomi.domain.TilkjentYtelseStatus
import no.nav.familie.ef.sak.økonomi.dto.TilkjentYtelseDTO
import no.nav.familie.ef.sak.økonomi.exception.UkjentOppdragException
import no.nav.familie.kontrakter.felles.oppdrag.OppdragStatus
import no.nav.familie.kontrakter.felles.oppdrag.Utbetalingsoppdrag
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping(path = ["/api/tilkjentytelse"])
@ProtectedWithClaims(issuer = "azuread")
class TilkjentYtelseController(private val tilkjentYtelseService: TilkjentYtelseService) {

    @PostMapping
    fun opprettTilkjentYtelse(@RequestBody tilkjentYtelseDTO: TilkjentYtelseDTO): ResponseEntity<Long> {

        tilkjentYtelseDTO.valider()

        val tilkjentYtelseId = tilkjentYtelseService.opprettTilkjentYtelseOgIverksettUtbetaling(tilkjentYtelseDTO)

        val location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .pathSegment(tilkjentYtelseId.toString())
                .build().toUri()

        return ResponseEntity.created(location).build()
    }

    @GetMapping("{tilkjentYtelseId}")
    fun hentTilkjentYtelse(@PathVariable tilkjentYtelseId: UUID): ResponseEntity<TilkjentYtelseDTO> {
        val tilkjentYtelseDto = tilkjentYtelseService.hentTilkjentYtelseDto(tilkjentYtelseId)

        return ResponseEntity.ok(tilkjentYtelseDto)
    }

    @GetMapping("{tilkjentYtelseId}/status")
    fun hentTilkjentYtelseStatus(@PathVariable tilkjentYtelseId: UUID): ResponseEntity<TilkjentYtelseStatus> {
        val tilkjentYtelseDto = tilkjentYtelseService.hentTilkjentYtelseDto(tilkjentYtelseId)

        return ResponseEntity.ok(tilkjentYtelseDto.status)
    }


    @GetMapping("{tilkjentYtelseId}/utbetaling")
    fun hentUtbetaling(@PathVariable tilkjentYtelseId: UUID): ResponseEntity<Utbetalingsoppdrag> {
        val utbetalingsoppdrag = tilkjentYtelseService.hentUtbetalingsoppdrag(tilkjentYtelseId)

        return utbetalingsoppdrag?.let { ResponseEntity.ok(utbetalingsoppdrag) } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("{tilkjentYtelseId}")
    fun opphørUtbetaling(@PathVariable tilkjentYtelseId: UUID): ResponseEntity<Long> {
        val opphørtTilkjentYtelseId = tilkjentYtelseService.avsluttTilkjentYtelseOgOpphørUtbetalingsoppdrag(tilkjentYtelseId)

        val location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .pathSegment(opphørtTilkjentYtelseId.toString())
                .build().toUri()

        return ResponseEntity.accepted().location(location).build()
    }

    @GetMapping("{tilkjentYtelseId}/utbetaling/status")
    fun hentStatusUtbetaling(@PathVariable tilkjentYtelseId: UUID): ResponseEntity<OppdragStatus> {
        try {
            val status = tilkjentYtelseService.hentOppdragStatus(tilkjentYtelseId)
            return ResponseEntity.ok(status)
        } catch(e: UkjentOppdragException) {
            return ResponseEntity.notFound().build()
        }

    }

}