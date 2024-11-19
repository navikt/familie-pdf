package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/test-pdf")
class TestPdfController {
    private val testPdfService = TestPdfService()

    @CrossOrigin(origins = ["http://localhost:5173"])
    @GetMapping("/hent-pdf-med-standarder")
    fun hentPdfFraResourceMedStandarder(): PdfMedStandarder = testPdfService.opprettTestPdfMedStandarder()
}
