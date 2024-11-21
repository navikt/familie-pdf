package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/test-pdf")
class TestPdfController {
    private val pdfService = PdfService()
    private val testPdfService = TestPdfService(pdfService)

    @GetMapping("/pdf-med-standarder")
    fun hentPdfFraResourceMedStandarder(): PdfMedStandarder = testPdfService.opprettTestPdfMedStandarder()

    @PostMapping("/pdf-med-standarder")
    fun opprettPdfMedValidering(
        @RequestBody søknad: Map<String, Any>,
    ): PdfMedStandarder = testPdfService.opprettPdfMedStandarder(søknad)
}
