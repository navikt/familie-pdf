package no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.JsonLeser.lesJson
import no.nav.familie.pdf.pdf.domain.PdfMedStandarder

class TestPdfService {
    private val pdfOppretterService = PdfService()

    fun opprettTestPdfMedStandarder(): PdfMedStandarder {
        val feltMap = lesJson()
        return pdfOppretterService.opprettPdfMedStandarder(feltMap)
    }
}
