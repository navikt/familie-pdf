package no.nav.familie.pdf.søknad

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SøknadControllerTest {
    private val søknadController = SøknadController()

    @Test
    fun `skal returnere OK`() {
        val søknadPdf = søknadController.lagPdfForOvergangsstønad()

        assertTrue(søknadPdf.contains("OK - overgangsstønad"))
    }
}
