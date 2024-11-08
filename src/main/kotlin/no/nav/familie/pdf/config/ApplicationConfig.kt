package no.nav.familie.pdf.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.nav.familie.http.config.RestTemplateBuilderBean
import no.nav.familie.http.interceptor.BearerTokenClientInterceptor
import no.nav.familie.http.interceptor.BearerTokenExchangeClientInterceptor
import no.nav.familie.http.interceptor.ConsumerIdClientInterceptor
import no.nav.familie.http.interceptor.MdcValuesPropagatingClientInterceptor
import no.nav.familie.kafka.KafkaErrorHandler
import no.nav.familie.log.filter.LogFilter
import no.nav.familie.log.filter.RequestTimeFilter
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootConfiguration
@ComponentScan("no.nav.familie.pdf")
@ConfigurationPropertiesScan
@EnableOAuth2Client(cacheEnabled = true)
@EnableJwtTokenValidation(ignore = ["org.springframework", "org.springdoc"])
@EnableScheduling
@Import(
    BearerTokenClientInterceptor::class,
    BearerTokenExchangeClientInterceptor::class,
    RestTemplateBuilderBean::class,
    MdcValuesPropagatingClientInterceptor::class,
    ConsumerIdClientInterceptor::class,
    KafkaErrorHandler::class,
)
class ApplicationConfig {
    private val logger = LoggerFactory.getLogger(ApplicationConfig::class.java)

    @Bean
    fun kotlinModule(): KotlinModule = KotlinModule.Builder().build()

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
}
