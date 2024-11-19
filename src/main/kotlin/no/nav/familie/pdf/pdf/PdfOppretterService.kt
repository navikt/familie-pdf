package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.pdf.JsonLeser.lesJson
import no.nav.familie.pdf.pdf.PdfOppretterUtils.lagDokument
import no.nav.familie.pdf.pdf.PdfOppretterUtils.lagPdfADocument
import no.nav.familie.pdf.pdf.PdfValidator.validerPdf
import no.nav.familie.pdf.pdf.domain.PdfMedStandarder
import no.nav.familie.pdf.pdf.domain.Standarder

class PdfOppretterService {
    fun lagRessursPdfMedStandarder(): PdfMedStandarder {
        val feltMap = lesJson()
        return lagPdfMedStandarder(feltMap)
    }

    fun lagPdfMedStandarder(feltMap: Map<String, Any>): PdfMedStandarder {
        val pdf = lagPdf(feltMap)
        val pdfMedStandarder =
            PdfMedStandarder(
                pdf,
                Standarder(
                    ua1 = validerPdf(pdf, "ua1"),
                    ua2 = validerPdf(pdf, "ua2"),
                    `1a` = validerPdf(pdf, "1a"),
                    `1b` = validerPdf(pdf, "1b"),
                    `2a` = validerPdf(pdf, "2a"),
                    `2b` = validerPdf(pdf, "2b"),
                    `2u` = validerPdf(pdf, "2u"),
                    `3a` = validerPdf(pdf, "3a"),
                    `3b` = validerPdf(pdf, "3b"),
                    `3u` = validerPdf(pdf, "3u"),
                    `4` = validerPdf(pdf, "4"),
                    `4f` = validerPdf(pdf, "4f"),
                    `4e` = validerPdf(pdf, "4e"),
                ),
            )
        return pdfMedStandarder
    }

    fun lagPdf(feltMap: Map<String, Any>): ByteArray {
        feltMap.values.forEach { value ->
            requireNotNull(value) { "feltMap sitt label eller verdiliste er tom." }
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)
        lagDokument(pdfADokument, feltMap)

        return byteArrayOutputStream.toByteArray()
    }
}
