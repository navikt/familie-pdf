package no.nav.familie.pdf.mottak

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
@ConfigurationPropertiesScan
class ApplicationLocalConfig
