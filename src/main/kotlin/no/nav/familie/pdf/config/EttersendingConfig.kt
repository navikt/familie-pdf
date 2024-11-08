package no.nav.familie.pdf.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URL

@ConfigurationProperties("ettersending")
data class EttersendingConfig(
    val ettersendingUrl: URL,
)
