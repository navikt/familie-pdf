package no.nav.familie.pdf.pdf

import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfViewerPreferences
import com.itextpdf.kernel.xmp.XMPMeta
import com.itextpdf.kernel.xmp.XMPMetaFactory
import com.itextpdf.pdfa.PdfADocument

/**
 * Metadata settes på to måter for ulike behov:
 * 1. `documentInfo`: Grunnleggende metadata for visning i PDF-lesere.
 * 2. `setXmpMetadata`: Maskinlesbar metadata for standarder som PDF/UA.
 */
object UtilsMetaData {
    fun leggtilMetaData(pdfADokument: PdfADocument, feltMap: Map<String, Any>) {
        val skaperAvPDF = "navikt/familie-pdf"
        val tittel = feltMap["label"].toString()

        pdfADokument.documentInfo.apply {
            this.title = tittel
            this.creator = skaperAvPDF
        }

        val xmpMeta = lagXmpMeta(skaperAvPDF, tittel)
        pdfADokument.setXmpMetadata(xmpMeta)

        pdfADokument.catalog.apply {
            put(PdfName.Lang, PdfString("no-NB")) // TODO: Gjør dynamisk
            viewerPreferences = PdfViewerPreferences().setDisplayDocTitle(true)
        }
    }
    private fun lagXmpMeta(
        skaperAvPDF: String,
        tittel: String,
    ): XMPMeta {
        val xmpMeta =
            XMPMetaFactory.create().apply {
                setProperty(
                    "http://purl.org/dc/elements/1.1/",
                    "dc:title",
                    tittel,
                )
                setProperty("http://purl.org/dc/elements/1.1/", "dc:creator", skaperAvPDF)
                //Angir delnummeret for PDF/UA-samsvar (2 for UA-2, 1 for UA-1)
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

    private fun getCurrentYear(): String {
        return java.time.Year.now().toString()
    }
}
