package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import no.nav.familie.pdf.pdf.PdfController
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.FileOutputStream


internal class PdfControllerTest {
private val pdfController = PdfController()

    @Test
    fun `generer pdf for graferdsstønad`() {

        val textByPage = `Test av skjema`("/gravferdsstønad.json", "delme-1.pdf" )

        assertTrue(textByPage[1].contains("I tillegg til dette skjemaet"))
    }

    @Test
    fun `generer pdf for arbeid- og utdanningssøknad `() {

        val textByPage = `Test av skjema`("/arbeid-utdannings-søknad.json", "delme-2.pdf" )

        assertTrue(textByPage[3].contains("Legeerklæringen må inneholde:"))
    }

    @Test
    fun `generer pdf for avtale om medfinansiering`() {

        val textByPage = `Test av skjema`("/avtale-om-medfinansiering.json", "delme-3.pdf" )

        assertTrue(textByPage[0].contains("Forskriften §14-1"))
    }

    @Test
    fun `generer pdf for barnetilsyn`() {

        val textByPage = `Test av skjema`("/barnetilsyn.json", "delme-4.pdf" )

        assertTrue(textByPage[2].contains("Nei, jeg bor alene med barn eller jeg er gravid og bor alene"))
    }

    private fun `Test av skjema`(jsonFile: String, skrivTilFil: String?): List<String> {
        val søknad = no.nav.familie.pdf.pdf.lokalKjøring.JsonLeser.lesSøknadJson(jsonFile)

        val pdfResponse: ByteArray = pdfController.opprettPdf(søknad)

        assertNotNull(pdfResponse)

        val textByPage = mutableListOf<String>()

        ByteArrayInputStream(pdfResponse).use { inputStream ->
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)

            for (i in 1..pdfDocument.numberOfPages) {
                val page = pdfDocument.getPage(i)
                val text = PdfTextExtractor.getTextFromPage(page)
                textByPage.add(text)
            }

            pdfDocument.close()
        }

        if (skrivTilFil != null) {
            writeBytesToFile(pdfResponse, skrivTilFil)
        }

        return textByPage
    }

    fun writeBytesToFile(byteArray: ByteArray, filePath: String) {
        val outputStream = FileOutputStream(filePath)
        outputStream.write(byteArray)
        outputStream.close()
    }

}