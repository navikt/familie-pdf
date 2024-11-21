package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Unprotected
@RequestMapping("api/test-pdf")
class TestPdfController {
    private val testPdfService = TestPdfService()
    private val pdfService = PdfService()

    @CrossOrigin(origins = ["http://localhost:5173"])
    @GetMapping("/pdf-med-standarder")
    fun hentPdfFraResourceMedStandarder(): PdfMedStandarder = testPdfService.opprettTestPdfMedStandarder()

    @CrossOrigin(origins = ["http://localhost:5173"])
    @PostMapping("/pdf-med-standarder")
    fun opprettPdfMedValidering(
        @RequestBody søknad: Map<String, Any>,
    ): PdfMedStandarder = pdfService.opprettPdfMedStandarder(søknad)
}
