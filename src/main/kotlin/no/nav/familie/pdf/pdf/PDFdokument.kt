package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfAConformance
import com.itextpdf.kernel.pdf.PdfOutputIntent
import com.itextpdf.kernel.pdf.PdfVersion
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.layout.Document
import com.itextpdf.pdfa.PdfADocument
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.genererInnholdsfortegnelseOppføringer
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggTilInnholdsfortegnelse
import no.nav.familie.pdf.pdf.pdfElementer.leggTilForside
import no.nav.familie.pdf.pdf.pdfElementer.leggTilSeksjoner

object PDFdokument {
    fun lagPdfADocument(byteArrayOutputStream: ByteArrayOutputStream): PdfADocument {
        val pdfWriter =
            PdfWriter(
                byteArrayOutputStream,
                WriterProperties().setPdfVersion(PdfVersion.PDF_2_0),
            )
        val inputStream = javaClass.getResourceAsStream("/colorProfile/sRGB_CS_profile.icm")
        val pdfADokument =
            PdfADocument(
                pdfWriter,
                PdfAConformance.PDF_A_4,
                PdfOutputIntent("Custom", "", null, "sRGB IEC61966-2.1", inputStream),
            )
        pdfADokument.setTagged()

        return pdfADokument
    }

    fun lagSøknadskvittering(
        pdfADokument: PdfADocument,
        feltMap: FeltMap,
        v2: Boolean,
    ): Document {
        leggtilMetaData(pdfADokument, feltMap)

        return Document(pdfADokument).apply {
            settFont(FontStil.REGULAR)
            if (feltMap.bunntekst == null ||
                (feltMap.bunntekst.upperleft == null && feltMap.bunntekst.upperMiddle == null && feltMap.bunntekst.upperRight == null)
            ) {
                setMargins(36f, 36f, 44f, 36f)
            } else {
                setMargins(36f, 36f, 58f, 36f)
            }

            if (feltMap.pdfConfig.harInnholdsfortegnelse) {
                leggTilInnholdsfortegnelse(feltMap, genererInnholdsfortegnelseOppføringer(feltMap, v2))
            } else {
                leggTilForside(feltMap.label, feltMap.skjemanummer)
            }

            leggTilSeksjoner(feltMap, v2)
            leggTilSidevisning(pdfADokument)

            close()
        }
    }
}
