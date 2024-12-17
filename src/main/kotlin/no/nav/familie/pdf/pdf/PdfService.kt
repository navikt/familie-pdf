package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.PdfUtils.lagDokument
import no.nav.familie.pdf.pdf.PdfUtils.lagPdfADocument
import no.nav.familie.pdf.pdf.domain.FeltMap

class PdfService {
    fun opprettPdf(feltMap: FeltMap): ByteArray {
        requireNotNull(feltMap.label) { "feltMap sitt label er tom." }
        requireNotNull(feltMap.verdiliste) { "feltMap sittverdiliste er tom." }

        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        lagDokument(pdfADokument, feltMap)

        return byteArrayOutputStream.toByteArray()
    }
}
