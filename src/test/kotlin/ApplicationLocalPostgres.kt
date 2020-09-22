package no.nav.familie.ef.sak

import no.nav.familie.ef.sak.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.testcontainers.containers.PostgreSQLContainer
import java.util.*

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class ApplicationLocalPostgres

fun main(args: Array<String>) {

    val sqlContainer = KPostgreSQLContainer("postgres")
            .withDatabaseName("familie-ef-sak")
            .withUsername("postgres")
            .withPassword("test")

    sqlContainer.start()

    val properties = Properties()
    properties["SPRING_DATASOURCE_URL_OVERRIDE"] = sqlContainer.jdbcUrl
    properties["SPRING_DATASOURCE_USERNAME_OVERRIDE"] = sqlContainer.username
    properties["SPRING_DATASOURCE_PASSWORD_OVERRIDE"] = sqlContainer.password
    properties["SPRING_DATASOURCE_DRIVER_OVERRIDE"] = "org.postgresql.Driver"

    SpringApplicationBuilder(ApplicationConfig::class.java)
            .profiles("local-postgres",
                      "mock-oauth",
                      "mock-integrasjoner",
                      "mock-pdl",
                      "mock-kodeverk")
            .properties(properties)
            .run(*args)
}

// Hack needed because testcontainers use of generics confuses Kotlin
class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName)