package no.nav.familie.pdf.pdf.lokalKjøring

import no.nav.familie.pdf.pdf.PdfService
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.familie.pdf.pdf.domain.PdfStandard
import no.nav.familie.pdf.pdf.domain.Standard
import no.nav.familie.pdf.pdf.lokalKjøring.JsonLeser.lesSøknadJson

class LokalPdfService(
    private val pdfService: PdfService,
) {
    fun opprettTestPdfMedStandarder(): PdfMedStandarder {
        val feltMap = lesSøknadJson()
        return opprettPdfMedStandarder(feltMap)
    }

    fun opprettPdfMedStandarder(feltMap: FeltMap): PdfMedStandarder {
        val pdf = pdfService.opprettPdf(feltMap)
        val pdfMedStandarder =
            PdfMedStandarder(
                pdf,
                createStandarder(pdf),
            )
        return pdfMedStandarder
    }

    private fun createStandarder(pdf: ByteArray): Map<PdfStandard, Standard> {
        val standarder =
            PdfStandard.values().associateWith {
                PdfValidator.validerPdf(pdf, it.standard)
            }
        return standarder
    }
}
