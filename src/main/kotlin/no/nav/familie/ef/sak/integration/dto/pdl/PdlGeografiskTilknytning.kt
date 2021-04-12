package no.nav.familie.ef.sak.integration.dto.pdl

data class PdlHentGeografiskTilknytning(val GeografiskTilknytning: PdlGeografiskTilknytning)

data class PdlGeografiskTilknytning(val gtType: GeografiskTilknytningType,
                                    val gtKommune: String?,
                                    val gtBydel: String?,
                                    val gtLand: String?){
    fun hentGeografiskTilknytning(): String {
        return when (gtType) {
            GeografiskTilknytningType.KOMMUNE -> gtKommune!!
            GeografiskTilknytningType.BYDEL -> gtBydel!!
            GeografiskTilknytningType.UTLAND -> gtLand!!
            GeografiskTilknytningType.UDEFINERT -> "ingen geografisk tilknytning"
        }
    }
}

enum class GeografiskTilknytningType {
    KOMMUNE,
    BYDEL,
    UTLAND,
    UDEFINERT
}

data class PdlGeografiskTilknytningRequest(val variables: PdlGeografiskTilknytningVariables,
                                           val query: String)

data class PdlGeografiskTilknytningVariables(val ident: String)