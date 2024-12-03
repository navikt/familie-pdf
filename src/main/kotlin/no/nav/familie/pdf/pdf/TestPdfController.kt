package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/test-pdf")
@ProtectedWithClaims(
    issuer = "azuread",
)
class TestPdfController {
    private val pdfService = PdfService()
    private val testPdfService = TestPdfService(pdfService)

    @CrossOrigin(origins = ["http://localhost:5173"])
    @GetMapping("/pdf-med-standarder")
    fun hentPdfFraResourceMedStandarder(): PdfMedStandarder = testPdfService.opprettTestPdfMedStandarder()

    @CrossOrigin(origins = ["http://localhost:5173"])
    @PostMapping("/pdf-med-standarder")
    fun opprettPdfMedValidering(
        @RequestBody søknad: Map<String, Any>,
    ): PdfMedStandarder = testPdfService.opprettPdfMedStandarder(søknad)
}
