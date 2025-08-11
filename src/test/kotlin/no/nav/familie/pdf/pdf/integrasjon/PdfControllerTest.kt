package no.nav.familie.pdf.no.nav.familie.pdf.pdf.integrasjon

import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedBunntekstOgVannmerkeTekst
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedVerdiliste
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.FileOutputStream
import kotlin.test.assertEquals

class PdfControllerTest : IntegrasjonSpringRunnerTest() {
    private val skrivTilFil = false

    @BeforeEach
    fun setUp() {
        headers.setBearerAuth(søkerBearerToken())
    }

    @Test
    fun `opprett-pdf OK request`() {
        val feltMap = lagMedVerdiliste()

        val response: ResponseEntity<ByteArray> =
            restTemplate.exchange(
                localhost("/api/pdf/v1/opprett-pdf"),
                HttpMethod.POST,
                HttpEntity(feltMap, headers),
            )

        assertEquals(response.statusCode, HttpStatus.OK)
    }

    @Test
    fun `opprett-pdf V3 OK request`() {
        val feltMap = lagMedBunntekstOgVannmerkeTekst()

        val response: ResponseEntity<ByteArray> =
            restTemplate.exchange(
                localhost("/api/pdf/v3/opprett-pdf"),
                HttpMethod.POST,
                HttpEntity(feltMap, headers),
            )

        assertEquals(response.statusCode, HttpStatus.OK)

        writeBytesToFile(response.body, "medVannmerke.pdf")
    }

    fun writeBytesToFile(
        byteArray: ByteArray,
        filePath: String,
    ) {
        if (skrivTilFil) {
            val outputStream = FileOutputStream(filePath)
            outputStream.write(byteArray)
            outputStream.close()
        }
    }
}
