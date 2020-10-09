package no.nav.familie.ef.sak.api.fagsak

import no.nav.familie.ef.sak.repository.domain.Stønadstype
import java.util.*

data class FagsakDto (
        val id: UUID,
        val personIdent: String,
        val stønadstype: Stønadstype,
        val behandlinger: List<BehandlingDto>
)

