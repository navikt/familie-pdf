package no.nav.familie.pdf.no.nav.familie.pdf.pdf.språkContextTest

import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpråkContextTest {
    @BeforeEach
    fun setup() {
        SpråkContext.fjernSpråk()
    }

    @AfterEach
    fun cleanup() {
        SpråkContext.fjernSpråk()
    }

    @Test
    fun `standard språk er norsk bokmål`() {
        assertEquals("nb", SpråkContext.brukSpråk())
    }

    @Test
    fun `kan sette og hente språk`() {
        SpråkContext.setSpråk("en")
        assertEquals("en", SpråkContext.brukSpråk())
    }

    @Test
    fun `fjernSpråk setter tilbake til standard nb`() {
        SpråkContext.setSpråk("fr")
        SpråkContext.fjernSpråk()
        assertEquals("nb", SpråkContext.brukSpråk())
    }
}
