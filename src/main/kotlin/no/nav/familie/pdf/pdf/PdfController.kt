package no.nav.familie.pdf.pdf

import no.nav.familie.sikkerhet.EksternBrukerUtils
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/pdf")
@ProtectedWithClaims(
    issuer = EksternBrukerUtils.ISSUER_TOKENX,
    claimMap = ["acr=Level4"],
)
class PdfController {
    private val pdfService = PdfService()

    @PostMapping("/opprett-pdf")
    fun opprettPdf(
        @RequestBody søknad: Map<String, Any>,
    ): ByteArray = pdfService.opprettPdf(søknad)
}
