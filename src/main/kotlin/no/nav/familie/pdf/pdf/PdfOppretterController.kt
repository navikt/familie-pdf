package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.types.PdfMedStandarder
import no.nav.familie.pdf.pdf.types.lagPdfMedStandarder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class PdfOppretterController {
    @PostMapping("/lag-pdf")
    fun lagPdf(
        @RequestBody søknad: Map<String, Any>,
    ): ByteArray {
        val generertPdf = PdfOppretterService().lagPdf(søknad)
        return generertPdf
    }

    @CrossOrigin(origins = ["http://localhost:5173"])
    @PostMapping("generate-pdf")
    fun lagPdfMedValidering(
        @RequestBody søknad: Map<String, Any>,
    ): PdfMedStandarder {
        val pdf =
            PdfOppretterService()
                .lagPdf(søknad)

        return lagPdfMedStandarder(pdf)
    }

    @CrossOrigin(origins = ["http://localhost:5173"])
    @GetMapping("lag-pdf-med-standarder")
    fun hentPdfFraResourceMedStandarder(): PdfMedStandarder {
        val søknad = lesJSON()
        val pdf =
            PdfOppretterService()
                .lagPdf(søknad)
        return lagPdfMedStandarder(pdf)
    }
}
