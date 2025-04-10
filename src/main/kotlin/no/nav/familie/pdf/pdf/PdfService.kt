package no.nav.familie.pdf.pdf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.PDFdokument.lagPdfADocument
import no.nav.familie.pdf.pdf.PDFdokument.lagSøknadskvittering
import no.nav.familie.pdf.pdf.domain.FeltMap

class PdfService {
    fun opprettPdf(feltMap: FeltMap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        val test = jacksonObjectMapper().writeValueAsString(feltMap)
        val test2 = test.replace("\\t", "")
        val test3 = jacksonObjectMapper().readValue(test2, FeltMap::class.java)
        lagSøknadskvittering(pdfADokument, test3)

        return byteArrayOutputStream.toByteArray()
    }
}
