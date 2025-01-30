package no.nav.familie.pdf.no.nav.familie.pdf.pdf.integrasjon

import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedVerdiliste
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

class PdfControllerTest : IntegrasjonSpringRunnerTest() {
    @BeforeEach
    fun setUp() {
        headers.setBearerAuth(søkerBearerToken())
    }

    @Test
    fun `opprett-pdf OK request`() {
        val feltMap = lagMedVerdiliste()

        val response: ResponseEntity<ByteArray> =
            restTemplate.exchange(
                localhost("/api/v1/pdf/opprett-pdf"),
                HttpMethod.POST,
                HttpEntity(feltMap, headers),
            )

        assertEquals(response.statusCode, HttpStatus.OK)
    }
}
