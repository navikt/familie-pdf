package no.nav.familie.pdf.no.nav.familie.pdf.pdf.integrasjon

import no.nav.familie.pdf.ApplicationLocalConfig
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ApplicationLocalConfig::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrasjonstest")
@EnableMockOAuth2Server
abstract class IntegrasjonSpringRunnerTest {
    protected val headers = HttpHeaders()

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    @LocalServerPort
    private var port: Int? = 0

    protected fun getPort(): String = port.toString()

    protected fun localhost(uri: String): String = LOCALHOST + getPort() + uri

    fun søkerBearerToken(): String {
        val clientId = "lokal:teamfamilie:familie-pdf"
        val randomId = UUID.randomUUID().toString()
        return mockOAuth2Server
            .issueToken(
                issuerId = "azuread",
                subject = randomId,
                audience = "aud-localhost",
                claims = mapOf("azp" to clientId, "oid" to randomId, "roles" to listOf("access_as_application")),
            ).serialize()
    }

    companion object {
        private const val LOCALHOST = "http://localhost:"
    }
}
