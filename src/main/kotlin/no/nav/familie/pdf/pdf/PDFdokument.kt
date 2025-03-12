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
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.beregnAntallSider
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggInnholdsfortegnelsenFørst
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggTilInnholdsfortegnelse
import no.nav.familie.pdf.pdf.pdfElementer.Innholdsfortegnelse.leggTilSeksjoner
import no.nav.familie.pdf.pdf.pdfElementer.InnholdsfortegnelseOppføringer

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
    ): Document {
        val skalHaInnholdsfortegnelse = feltMap.pdfConfig.harInnholdsfortegnelse
        val innholdsfortegnelse = mutableListOf<InnholdsfortegnelseOppføringer>()
        val lengdeInnholdsfortegnelse = if (skalHaInnholdsfortegnelse) beregnAntallSider(feltMap, innholdsfortegnelse) else 0

        leggtilMetaData(pdfADokument, feltMap)

        return Document(pdfADokument).apply {
            settFont(FontStil.REGULAR)
            setMargins(36f, 36f, 44f, 36f)

            leggTilSeksjoner(
                feltMap,
                innholdsfortegnelse,
                pdfADokument,
                lengdeInnholdsfortegnelse,
            )
            if (skalHaInnholdsfortegnelse) {
                leggTilInnholdsfortegnelse(feltMap.label, innholdsfortegnelse, feltMap.skjemanummer)
                leggInnholdsfortegnelsenFørst(lengdeInnholdsfortegnelse, pdfADokument)
            }

            leggTilSidevisning(pdfADokument)
            close()
        }
    }
}
