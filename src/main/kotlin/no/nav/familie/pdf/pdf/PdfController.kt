package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.infrastruktur.UnleashNextService
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkKontekst
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/pdf")
@ProtectedWithClaims(
    issuer = "azuread",
)
class PdfController(
    unleashNextService: UnleashNextService,
) {
    private val pdfService = PdfService(unleashNextService)

    data class PdfResponse(
        val pdf: ByteArray,
    )

    @PostMapping("/v1/opprett-pdf")
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

    @PostMapping("/opprett-pdf/som-json", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun opprettPdfSomJson(
        @RequestBody søknad: FeltMap,
    ): PdfResponse {
        try {
            SpråkKontekst.settSpråk(søknad.pdfConfig.språk)
            val returverdi = pdfService.opprettPdf(søknad)
            return PdfResponse(returverdi)
        } finally {
            SpråkKontekst.tilbakestillSpråk()
        }
    }

    @PostMapping("/v2/opprett-pdf")
    fun opprettPdfV2(
        @RequestBody søknad: FeltMap,
    ): ByteArray {
        try {
            SpråkKontekst.settSpråk(søknad.pdfConfig.språk)
            return pdfService.opprettPdf(søknad, true)
        } finally {
            SpråkKontekst.tilbakestillSpråk()
        }
    }
}
