package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class PdfOppretterController {
    val pdfOppretterService = PdfOppretterService()

    @PostMapping("/lag-pdf")
    fun lagPdf(
        @RequestBody søknad: Map<String, Any>,
    ): ByteArray = pdfOppretterService.lagPdf(søknad)

    @CrossOrigin(origins = ["http://localhost:5173"])
    @PostMapping("generate-pdf")
    fun lagPdfMedValidering(
        @RequestBody søknad: Map<String, Any>,
    ): PdfMedStandarder = pdfOppretterService.lagPdfMedStandarder(søknad)

    @CrossOrigin(origins = ["http://localhost:5173"])
    @GetMapping("lag-pdf-med-standarder")
    fun hentPdfFraResourceMedStandarder(): PdfMedStandarder = pdfOppretterService.lagRessursPdfMedStandarder()
}
