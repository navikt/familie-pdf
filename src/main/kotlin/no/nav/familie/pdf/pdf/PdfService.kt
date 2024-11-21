package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.PdfUtils.lagDokument
import no.nav.familie.pdf.pdf.PdfUtils.lagPdfADocument

class PdfService {
    fun opprettPdf(feltMap: Map<String, Any>): ByteArray {
        feltMap.values.forEach { value ->
            requireNotNull(value) { "feltMap sitt label eller verdiliste er tom." }
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        lagDokument(pdfADokument, feltMap)

        return byteArrayOutputStream.toByteArray()
    }
}
