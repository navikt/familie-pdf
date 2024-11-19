package no.nav.familie.pdf.mottak

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PdfControllerTest {
    private val mottakClient: MottakClient = mockk()
    private val søknadController = PdfController(mottakClient)

    @Test
    fun `skal returnere OK`() {
        every { mottakClient.helsesjekk() } returns "OK - overgangsstønad"
        val søknadPdf = søknadController.testerMottakOk()
        assertThat(søknadPdf).isEqualTo("OK - overgangsstønad")
    }
}
