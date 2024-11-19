package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.PdfUtils.lagDokument
import no.nav.familie.pdf.pdf.PdfUtils.lagPdfADocument
import no.nav.familie.pdf.pdf.PdfValidator.validerPdf
import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.familie.pdf.pdf.domain.PdfStandard
import no.nav.familie.pdf.pdf.domain.Standard

class PdfService {
    fun opprettPdfMedStandarder(feltMap: Map<String, Any>): PdfMedStandarder {
        val pdf = opprettPdf(feltMap)
        val pdfMedStandarder =
            PdfMedStandarder(
                pdf,
                createStandarder(pdf),
            )
        return pdfMedStandarder
    }

    fun opprettPdf(feltMap: Map<String, Any>): ByteArray {
        feltMap.values.forEach { value ->
            requireNotNull(value) { "feltMap sitt label eller verdiliste er tom." }
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        lagDokument(pdfADokument, feltMap)

        return byteArrayOutputStream.toByteArray()
    }

    private fun createStandarder(pdf: ByteArray): Map<PdfStandard, Standard> {
        val standarder = PdfStandard.values().associateWith { validerPdf(pdf, it.standard) }
        return standarder
    }
}
