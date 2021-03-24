package no.nav.familie.ef.sak.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.nav.familie.ef.sak.util.loggTid
import no.nav.familie.http.client.HttpClientUtil
import no.nav.familie.http.client.HttpRequestUtil
import no.nav.familie.http.config.RestTemplateAzure
import no.nav.familie.http.config.RestTemplateSts
import no.nav.familie.http.interceptor.ApiKeyInjectingClientInterceptor
import no.nav.familie.http.interceptor.ConsumerIdClientInterceptor
import no.nav.familie.http.interceptor.MdcValuesPropagatingClientInterceptor
import no.nav.familie.http.interceptor.StsBearerTokenClientInterceptor
import no.nav.familie.http.sts.AccessTokenResponse
import no.nav.familie.http.sts.StsAccessTokenFeilException
import no.nav.familie.http.sts.StsRestClient
import no.nav.familie.log.IdUtils
import no.nav.familie.log.NavHttpHeaders
import no.nav.familie.log.filter.LogFilter
import no.nav.familie.log.filter.RequestTimeFilter
import no.nav.familie.log.mdc.MDCConstants
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.core.configuration.ProxyAwareResourceRetriever
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.*
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.concurrent.ExecutionException

@SpringBootConfiguration
@ConfigurationPropertiesScan
@ComponentScan("no.nav.familie.prosessering", "no.nav.familie.ef.sak", "no.nav.familie.sikkerhet")
@EnableJwtTokenValidation(ignore = ["org.springframework", "springfox.documentation.swagger"])
@Import(RestTemplateAzure::class, RestTemplateSts::class, StsRestClient::class)
@EnableOAuth2Client(cacheEnabled = true)
@EnableScheduling
class ApplicationConfig {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun kotlinModule(): KotlinModule = KotlinModule()

    @Bean
    fun logFilter(): FilterRegistrationBean<LogFilter> {
        logger.info("Registering LogFilter filter")
        val filterRegistration = FilterRegistrationBean<LogFilter>()
        filterRegistration.filter = LogFilter()
        filterRegistration.order = 1
        return filterRegistration
    }

    @Bean
    fun requestTimeFilter(): FilterRegistrationBean<RequestTimeFilter> {
        logger.info("Registering RequestTimeFilter filter")
        val filterRegistration = FilterRegistrationBean<RequestTimeFilter>()
        filterRegistration.filter = RequestTimeFilter()
        filterRegistration.order = 2
        return filterRegistration
    }

    //Overskrever felles sin som bruker proxy, som ikke skal brukes på gcp
    @Bean
    @Primary
    fun restTemplateBuilder(): RestTemplateBuilder {
        return RestTemplateBuilder()
                .setConnectTimeout(Duration.of(2, ChronoUnit.SECONDS))
                .setReadTimeout(Duration.of(120, ChronoUnit.SECONDS))
    }

    @Bean("utenAuth")
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder,
                     consumerIdClientInterceptor: ConsumerIdClientInterceptor): RestOperations {
        return restTemplateBuilder
                .additionalInterceptors(consumerIdClientInterceptor,
                                        MdcValuesPropagatingClientInterceptor()).build()
    }

    @Bean
    fun apiKeyInjectingClientInterceptor(@Value("\${PDL_APIKEY}") pdlApiKey: String,
                                         @Value("\${PDL_URL}") pdlBaseUrl: String): ApiKeyInjectingClientInterceptor {
        val map = mapOf(Pair(URI.create(pdlBaseUrl), Pair(API_KEY_HEADER, pdlApiKey)))
        return ApiKeyInjectingClientInterceptor(map)
    }

    @Bean("stsMedApiKey")
    fun restTemplateSts(stsBearerTokenClientInterceptor: StsBearerTokenClientInterceptor,
                        consumerIdClientInterceptor: ConsumerIdClientInterceptor,
                        apiKeyInjectingClientInterceptor: ApiKeyInjectingClientInterceptor): RestOperations {
        return RestTemplateBuilder()
                .additionalInterceptors(consumerIdClientInterceptor,
                                        stsBearerTokenClientInterceptor,
                                        apiKeyInjectingClientInterceptor,
                                        MdcValuesPropagatingClientInterceptor()
                ).build()
    }

    // Brukes for sts issuer som brukes for sts validering. ApiKey blir lagt til når man henter metadata for STS_DISCOVERY_URL
    // trenger override pga token-support-test som allerede overridear denne i test scope
    @Bean
    @Primary
    @Profile("!integrasjonstest && !local")
    fun oidcResourceRetriever(@Value("\${STS_APIKEY}") stsApiKey: String): ProxyAwareResourceRetriever {
        val proxyAwareResourceRetriever = ProxyAwareResourceRetriever(null, false)
        proxyAwareResourceRetriever.headers = mapOf(API_KEY_HEADER to listOf(stsApiKey))
        return proxyAwareResourceRetriever
    }

    companion object {

        private const val API_KEY_HEADER = "x-nav-apiKey"
    }
}

@Component
@Primary
class StsRestClient(private val mapper: ObjectMapper,
                    @Value("\${STS_URL}") private val stsUrl: URI,
                    @Value("\${CREDENTIAL_USERNAME}") private val stsUsername: String,
                    @Value("\${CREDENTIAL_PASSWORD}") private val stsPassword: String,
                    @Value("\${STS_APIKEY:#{null}}") private val stsApiKey: String? = null) {

    private val client: HttpClient = HttpClientUtil.create()

    private var cachedToken: AccessTokenResponse? = null
    private var refreshCachedTokenTidspunkt = LocalDateTime.now()

    private val isTokenValid: Boolean
        get() {
            if (cachedToken == null) {
                return false
            }
            log.debug("Skal refreshe token: {}. Tiden nå er: {}",
                      refreshCachedTokenTidspunkt,
                      LocalTime.now())

            return refreshCachedTokenTidspunkt.isAfter(LocalDateTime.now())
        }

    val systemOIDCToken: String
        get() {
            if (isTokenValid) {
                log.info("Henter token fra cache")
                return cachedToken!!.access_token
            }
            log.info("Henter token fra STS")
            val callId = MDC.get(MDCConstants.MDC_CALL_ID) ?: IdUtils.generateId()
            val request =
                    HttpRequestUtil.createRequest(basicAuth(stsUsername, stsPassword))
                            .uri(stsUrl)
                            .header("Content-Type", "application/json")
                            .header(NavHttpHeaders.NAV_CALL_ID.asString(), callId)
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .timeout(Duration.ofSeconds(30)).apply {
                                if (!stsApiKey.isNullOrEmpty()) {
                                    header("x-nav-apiKey", stsApiKey)
                                }
                            }.build()

            val accessTokenResponse = loggTid(this::class, "systemOIDCToken", "hentToken") {
                try {
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenApply { obj: HttpResponse<String?> -> obj.body() }
                            .thenApply { it: String? ->
                                håndterRespons(it)
                            }
                            .get()
                } catch (e: InterruptedException) {
                    throw StsAccessTokenFeilException("Feil i tilkobling", e)
                } catch (e: ExecutionException) {
                    throw StsAccessTokenFeilException("Feil i tilkobling", e)
                }
            }
            if (accessTokenResponse != null) {
                cachedToken = accessTokenResponse
                refreshCachedTokenTidspunkt = LocalDateTime.now()
                        .plusSeconds(accessTokenResponse.expires_in)
                        .minusSeconds(accessTokenResponse.expires_in / 4) // Trekker av 1/4. Refresher etter 3/4 av levetiden
                return accessTokenResponse.access_token
            }
            throw StsAccessTokenFeilException("Manglende token")
        }

    private fun håndterRespons(it: String?): AccessTokenResponse {
        return try {
            mapper.readValue(it, AccessTokenResponse::class.java)
        } catch (e: IOException) {
            throw StsAccessTokenFeilException("Parsing av respons feilet", e)
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(StsRestClient::class.java)
        private fun basicAuth(username: String, password: String): String {
            return "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        }
    }

}