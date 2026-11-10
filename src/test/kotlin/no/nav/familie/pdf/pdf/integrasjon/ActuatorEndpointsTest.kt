package no.nav.familie.pdf.no.nav.familie.pdf.pdf.integrasjon

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus

class ActuatorEndpointsTest : IntegrasjonSpringRunnerTest() {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/internal",
            "/internal/health",
            "/internal/health/liveness",
            "/internal/health/readiness",
            "/internal/metrics",
        ],
    )
    fun `offentlige actuator-endepunkter er tilgjengelige uten token`(path: String) {
        val response = restTemplate.getForEntity(localhost(path), String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/internal/info", "/internal/status/isAlive"])
    fun `info og gammel helseprobe er ikke eksponert`(path: String) {
        val response = restTemplate.getForEntity(localhost(path), String::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `info-endepunktet er deaktivert`() {
        assertTrue(applicationContext.getBeansOfType(InfoEndpoint::class.java).isEmpty())
    }
}
