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
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.InnholdsfortegnelseOppføringer
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.kalkulerSideantallInnholdsfortegnelse
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggInnholdsfortegnelsenFørst
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggTilForsideMedInnholdsfortegnelse
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggTilSeksjonerOgOppdaterInnholdsfortegnelse
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggTilSidevisning

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

    fun lagDokument(
        pdfADokument: PdfADocument,
        feltMap: FeltMap,
        v2: Boolean,
    ) {
        val harInnholdsfortegnelse = feltMap.pdfConfig.harInnholdsfortegnelse
        val innholdsfortegnelse = mutableListOf<InnholdsfortegnelseOppføringer>()

        val sideantallInnholdsfortegnelse =
            if (harInnholdsfortegnelse) kalkulerSideantallInnholdsfortegnelse(feltMap, innholdsfortegnelse, v2) else 0

        leggtilMetaData(pdfADokument, feltMap)

        Document(pdfADokument).apply {
            settFont(FontStil.REGULAR)
            setMargins(36f, 36f, 44f, 36f)

            leggTilSeksjonerOgOppdaterInnholdsfortegnelse(
                feltMap,
                innholdsfortegnelse,
                pdfADokument,
                v2,
                sideantallInnholdsfortegnelse,
            )
            if (harInnholdsfortegnelse) {
                leggTilForsideMedInnholdsfortegnelse(feltMap.label, innholdsfortegnelse, feltMap.skjemanummer)
                leggInnholdsfortegnelsenFørst(sideantallInnholdsfortegnelse, pdfADokument)
            }

            leggTilSidevisning(pdfADokument)
            close()
        }
    }
}
