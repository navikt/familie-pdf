package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.PDFdokument.lagDokument
import no.nav.familie.pdf.pdf.PDFdokument.lagPdfADocument
import no.nav.familie.pdf.pdf.domain.FeltMap

class PdfService {
    fun opprettPdf(feltMap: FeltMap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        lagDokument(pdfADokument, feltMap)

        return byteArrayOutputStream.toByteArray()
    }
}

// yhei
