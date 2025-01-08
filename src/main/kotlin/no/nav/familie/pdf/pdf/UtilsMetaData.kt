package no.nav.familie.pdf.pdf

import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfViewerPreferences
import com.itextpdf.kernel.xmp.XMPMeta
import com.itextpdf.kernel.xmp.XMPMetaFactory
import com.itextpdf.pdfa.PdfADocument
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.Språk

/**
 * Metadata settes på to måter for ulike behov:
 * 1. `documentInfo`: Grunnleggende metadata for visning i PDF-lesere.
 * 2. `setXmpMetadata`: Maskinlesbar metadata for standarder som PDF/UA.
 */
object UtilsMetaData {
    fun leggtilMetaData(
        pdfADokument: PdfADocument,
        feltMap: FeltMap,
    ) {
        val skaperAvPdf = "navikt/familie-pdf"
        val tittel = feltMap.label
        val språk = feltMap.pdfConfig?.språk?.toString() ?: Språk.NB.toString()

        pdfADokument.documentInfo.apply {
            this.title = tittel
            this.creator = skaperAvPdf
        }

        val xmpMeta = lagXmpMeta(skaperAvPdf, tittel, språk)
        pdfADokument.xmpMetadata = xmpMeta

        pdfADokument.catalog.apply {
            put(PdfName.Lang, PdfString(språk))
            viewerPreferences = PdfViewerPreferences().setDisplayDocTitle(true)
        }
    }

    private fun lagXmpMeta(
        skaperAvPdf: String,
        tittel: String,
        språk: String,
    ): XMPMeta {
        val xmpMeta =
            XMPMetaFactory.create().apply {
                setLocalizedText(
                    "http://purl.org/dc/elements/1.1/",
                    "dc:title",
                    null,
                    "x-default",
                    tittel,
                )
                setProperty("http://purl.org/dc/elements/1.1/", "dc:creator", skaperAvPdf)
                setProperty("http://purl.org/dc/elements/1.1/", "dc:language", språk)
                // Angir delnummeret for PDF/UA-samsvar (2 for UA-2, 1 for UA-1)
                setProperty(
                    "http://www.aiim.org/pdfua/ns/id/",
                    "pdfuaid:part",
                    "1",
                )
                // Sett revisjonsåret for samsvarsstandarden til det nåværende året
                setProperty("http://www.aiim.org/pdfua/ns/id/", "pdfuaid:rev", getCurrentYear())
            }
        return xmpMeta
    }

    private fun getCurrentYear(): String =
        java.time.Year
            .now()
            .toString()
}
