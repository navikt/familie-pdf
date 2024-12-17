package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Profile("!prod")
@RestController
@RequestMapping("api/test-pdf")
@Unprotected
class TestPdfController {
    private val pdfService = PdfService()
    private val testPdfService = TestPdfService(pdfService)

    @CrossOrigin(origins = ["http://localhost:5173"])
    @GetMapping("/pdf-med-standarder")
    fun hentPdfFraResourceMedStandarder(): PdfMedStandarder = testPdfService.opprettTestPdfMedStandarder()

    @CrossOrigin(origins = ["http://localhost:5173"])
    @PostMapping("/pdf-med-standarder")
    fun opprettPdfMedValidering(
        @RequestBody søknad: FeltMap,
    ): PdfMedStandarder = testPdfService.opprettPdfMedStandarder(søknad)
}
