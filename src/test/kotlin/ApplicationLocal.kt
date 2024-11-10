package no.nav.familie.pdf

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.springframework.boot.builder.SpringApplicationBuilder

@EnableMockOAuth2Server
class ApplicationLocal : ApplicationLocalConfig()

fun main(args: Array<String>) {
    SpringApplicationBuilder(ApplicationLocal::class.java)
        .profiles().run(*args)
}
