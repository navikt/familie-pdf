package no.nav.familie.pdf.config

import io.getunleash.strategy.Strategy
import no.nav.familie.pdf.mottak.featuretoggle.ByEnvironmentStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CustomUnleashStrategies {
    @Bean
    fun strategies(): List<Strategy> = listOf(ByEnvironmentStrategy())
}
