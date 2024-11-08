package no.nav.familie.pdf.mottak

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.springframework.boot.builder.SpringApplicationBuilder

@EnableMockOAuth2Server
class ApplicationLocal : ApplicationLocalConfig()

fun main(args: Array<String>) {
    SpringApplicationBuilder(ApplicationLocal::class.java)
        .profiles(
            "local",
            "mock-integrasjon",
            "mock-dokument",
            "mock-ef-sak",
            "mock-pdf",
        ).run(*args)
}
