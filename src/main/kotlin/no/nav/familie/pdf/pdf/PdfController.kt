package no.nav.familie.pdf.pdf

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class PdfController {
    @PostMapping("/generate-pdf")
    fun generatePdf(
        @RequestBody søknad: Map<String, Any>,
    ): ByteArray {
        val generertPdf = PdfGenerator().lagPdf(søknad)
        return generertPdf
    }
}
