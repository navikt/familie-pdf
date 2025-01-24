package no.nav.familie.pdf.no.nav.familie.pdf.pdf.språkContextTest

import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkKontekst
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpråkKontekstTest {
    @BeforeEach
    fun setup() {
        SpråkKontekst.fjernSpråk()
    }

    @AfterEach
    fun cleanup() {
        SpråkKontekst.fjernSpråk()
    }

    @Test
    fun `standard språk er norsk bokmål`() {
        assertEquals("nb", SpråkKontekst.brukSpråk())
    }

    @Test
    fun `kan sette og hente språk`() {
        SpråkKontekst.setSpråk("en")
        assertEquals("en", SpråkKontekst.brukSpråk())
    }

    @Test
    fun `fjernSpråk setter tilbake til standard nb`() {
        SpråkKontekst.setSpråk("fr")
        SpråkKontekst.fjernSpråk()
        assertEquals("nb", SpråkKontekst.brukSpråk())
    }
}
