package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.infrastruktur.UnleashNextService
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkKontekst
import no.nav.familie.pdf.pdf.visningsvarianter.addWatermarkToPdf
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(javaClass)

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

    @PostMapping("/v3/opprett-pdf")
    fun opprettPdfV3(
        @RequestBody søknad: FeltMap,
    ): ByteArray {
        try {
            logger.info("Opprett PDF V3")
            SpråkKontekst.settSpråk(søknad.pdfConfig.språk)
            val returverdi = pdfService.opprettPdf(søknad)
            if (søknad.vannmerke.isNullOrBlank()) {
                return returverdi
            }
            logger.info("Legger til vannmerke i PDF")
            return addWatermarkToPdf(returverdi, søknad.vannmerke)
        } finally {
            SpråkKontekst.tilbakestillSpråk()
        }
    }
}
