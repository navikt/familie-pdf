package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.JsonLeser.lesSøknadJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonLeserTest {
    @Test
    fun `tester readJSON for søknadjson gir forventede verdier`() {
        // Act
        val resultat = lesSøknadJson()
        // Assert
        assertEquals("Søknad om overgangsstønad (NAV 15-00.01)", resultat["label"])
        val verdiliste = resultat["verdiliste"] as List<Map<String, Any>>
        assertEquals("Innsendingsdetaljer", verdiliste[0]["label"])
        val innsendingsdetaljer = verdiliste[0]["verdiliste"] as List<Map<String, Any>>
        assertEquals("Dato mottatt", innsendingsdetaljer[0]["label"])
        assertEquals("09.10.2024 09:59:35", innsendingsdetaljer[0]["verdi"])
    }
}
