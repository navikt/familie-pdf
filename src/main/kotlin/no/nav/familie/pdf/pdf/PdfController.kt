package no.nav.familie.pdf.pdf

import jakarta.validation.Valid
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/pdf")
@ProtectedWithClaims(
    issuer = "azuread",
)
class PdfController {
    private val pdfService = PdfService()

    @PostMapping("/opprett-pdf")
    fun opprettPdf(
        @Valid @RequestBody søknad: FeltMap,
    ): ByteArray = pdfService.opprettPdf(søknad)
}
