package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.PDFdokument.lagDokument
import no.nav.familie.pdf.pdf.PDFdokument.lagPdfADocument
import no.nav.familie.pdf.pdf.domain.FeltMap

class PdfService {
    fun opprettPdf(
        feltMap: FeltMap,
        v2: Boolean = false,
    ): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        lagDokument(pdfADokument, feltMap, v2)
        return byteArrayOutputStream.toByteArray()
    }
}

// yhei
