package no.nav.familie.ef.sak.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.ClassPathResource
import javax.servlet.Filter
import javax.servlet.http.HttpServletResponse

@Profile("dev", "local")
@Configuration
class GitConfig {

    @Bean
    fun placeholderConfigurer(): PropertySourcesPlaceholderConfigurer? {
        val propsConfig = PropertySourcesPlaceholderConfigurer()
        propsConfig.setLocation(ClassPathResource("git.properties"))
        propsConfig.setIgnoreResourceNotFound(true)
        propsConfig.setIgnoreUnresolvablePlaceholders(true)
        return propsConfig
    }

    @Bean
    fun gitFilter(@Value("\${git.commit.time}") commitTime: String,
                  @Value("\${git.branch}") branchName: String): Filter {
        return Filter { req, resp, chain ->
            (resp as HttpServletResponse).apply {
                setHeader("git-commit-time", commitTime)
                setHeader("git-branch", branchName)
            }
            chain.doFilter(req, resp)
        }
    }
}