package no.nav.familie.pdf.mottak.no.nav.familie.pdf.søknad

import no.nav.familie.pdf.søknad.SøknadController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SøknadControllerTest {

    private val søknadController = SøknadController()

    @Test
    fun `skal returnere OK`() {
        val søknadPdf = søknadController.lagPdfForOvergangsstønad()

        assertThat(søknadPdf).isEqualTo("Ok - overgangsstønad")
    }
}