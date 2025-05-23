package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.lokalKjøring.JsonLeser.lesSøknadJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonLeserTest {
    @Test
    fun `tester readJSON for søknadjson gir forventede verdier`() {
        // Act
        val resultat = lesSøknadJson()
        // Assert
        assertEquals("Søknad om overgangsstønad", resultat.label)
        val verdiliste = resultat.verdiliste
        assertEquals("Innsendingsdetaljer", verdiliste[0].label)
        val innsendingsdetaljer = verdiliste[0].verdiliste
        assertEquals("Dato mottatt", innsendingsdetaljer?.get(0)?.label)
        assertEquals("09.10.2024 09:59:35", innsendingsdetaljer?.get(0)?.verdi)
    }
}
