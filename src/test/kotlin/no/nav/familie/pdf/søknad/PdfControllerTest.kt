package no.nav.familie.pdf.søknad

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PdfControllerTest {
    private val søknadController = PdfController()

    @Test
    fun `skal returnere OK`() {
        val søknadPdf = søknadController.lagPdfForOvergangsstønad()

        assertThat(søknadPdf).isEqualTo("OK - overgangsstønad")
    }
}
