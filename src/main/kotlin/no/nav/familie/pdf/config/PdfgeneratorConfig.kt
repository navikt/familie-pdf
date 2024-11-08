package no.nav.familie.pdf.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("familie.ef.pdfgenerator")
data class PdfgeneratorConfig(
    val url: String,
)
