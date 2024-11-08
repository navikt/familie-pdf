package no.nav.familie.pdf.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties("familie.ef.integrasjoner")
data class IntegrasjonerConfig(
    val url: URI,
)
