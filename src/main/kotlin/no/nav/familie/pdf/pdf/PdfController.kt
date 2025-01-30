package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkKontekst
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
        @RequestBody søknad: FeltMap,
    ): ByteArray {

        try {
            SpråkKontekst.settSpråk(søknad.pdfConfig.språk)
            val returverdi = pdfService.opprettPdf(søknad)
            return returverdi
        } finally {
            SpråkKontekst.tilbakestillSpråk()
        }

    }
}
