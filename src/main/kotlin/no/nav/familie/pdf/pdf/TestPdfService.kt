package no.nav.familie.pdf.pdf

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.familie.pdf.pdf.domain.PdfStandard
import no.nav.familie.pdf.pdf.domain.Standard
import java.io.FileNotFoundException
import java.io.IOException

class TestPdfService(
    private val pdfService: PdfService,
    private val objectMapper: ObjectMapper =
        jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(JavaTimeModule()),
) {
    fun opprettTestPdfMedStandarder(): PdfMedStandarder {
        val feltMap = lesSøknadJson()
        return opprettPdfMedStandarder(feltMap)
    }

    fun opprettPdfMedStandarder(feltMap: Map<String, Any>): PdfMedStandarder {
        val pdf = pdfService.opprettPdf(feltMap)
        val pdfMedStandarder =
            PdfMedStandarder(
                pdf,
                createStandarder(pdf),
            )
        return pdfMedStandarder
    }

    private fun createStandarder(pdf: ByteArray): Map<PdfStandard, Standard> {
        val standarder =
            PdfStandard.values().associateWith {
                PdfValidator.validerPdf(pdf, it.standard)
            }
        return standarder
    }

    private fun lesSøknadJson(): Map<String, Any> {
        val jsonInputStream =
            this::class.java.getResourceAsStream("/søknad.json")
                ?: throw FileNotFoundException("Kan ikke lese søknad.json")

        return try {
            jsonInputStream.bufferedReader().use { reader ->
                objectMapper.readValue(reader, Map::class.java) as Map<String, Any>
            }
        } catch (e: IOException) {
            throw RuntimeException("Feil ved lesing av JSON-fil", e)
        }
    }
}
