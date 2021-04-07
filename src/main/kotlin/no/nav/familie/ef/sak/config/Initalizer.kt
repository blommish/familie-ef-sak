package no.nav.familie.ef.sak.config

import com.fasterxml.jackson.module.kotlin.readValue
import com.nimbusds.jose.proc.SimpleSecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import no.nav.familie.ef.sak.api.gui.VurderingController
import no.nav.familie.ef.sak.regler.Vilkårsregler
import no.nav.familie.ef.sak.repository.BehandlingRepository
import no.nav.familie.http.sts.StsRestClient
import no.nav.familie.kontrakter.felles.objectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@Component
class Initalizer(val stsRestClient: StsRestClient): ApplicationListener<ApplicationReadyEvent> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(p0: ApplicationReadyEvent) {
        //vurderingController.hentRegler()
        //DefaultJWTProcessor<SimpleSecurityContext>()
        //initializeVilkårsregler()
        //vurderingController.hentRegler()
        //try{RestTemplate().getForEntity<String>("http://localhost:8093/api/ping")} catch (e: Exception){
        //    logger.info("Feilet ping", e)
        //}
        try{
            //behandlingRepository.findAll()
            stsRestClient.systemOIDCToken
        } catch (e: Exception){

        }
    }

    private fun initializeVilkårsregler() {
        //val vilkårsregler = Vilkårsregler.VILKÅRSREGLER
        //logger.info("Initerer regler - ${vilkårsregler.vilkårsregler.size} regler")
    }

}