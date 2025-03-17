package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.PDFdokument.lagPdfADocument
import no.nav.familie.pdf.pdf.PDFdokument.lagSøknadskvittering
import no.nav.familie.pdf.pdf.domain.FeltMap
import org.slf4j.LoggerFactory

class PdfService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun opprettPdf(feltMap: FeltMap): ByteArray {
        logger.info("Feltmap: " + feltMap.toString())

        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        lagSøknadskvittering(pdfADokument, feltMap)

        return byteArrayOutputStream.toByteArray()
    }
}
