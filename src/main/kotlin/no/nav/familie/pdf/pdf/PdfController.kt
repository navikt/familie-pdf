package no.nav.familie.pdf.pdf

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/pdf")
class PdfController {
    private val pdfService = PdfService()

    @PostMapping("/opprett-pdf")
    fun opprettPdf(
        @RequestBody søknad: Map<String, Any>,
    ): ByteArray = pdfService.opprettPdf(søknad)
}
