package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Unprotected
@RequestMapping("api/pdf")
class PdfController {
    private val pdfOppretterService = PdfService()

    @PostMapping("/opprett-pdf")
    fun opprettPdf(
        @RequestBody søknad: Map<String, Any>,
    ): ByteArray = pdfOppretterService.opprettPdf(søknad)

    @CrossOrigin(origins = ["http://localhost:5173"])
    @PostMapping("/opprett-pdf-med-standarder")
    fun opprettPdfMedValidering(
        @RequestBody søknad: Map<String, Any>,
    ): PdfMedStandarder = pdfOppretterService.opprettPdfMedStandarder(søknad)
}
