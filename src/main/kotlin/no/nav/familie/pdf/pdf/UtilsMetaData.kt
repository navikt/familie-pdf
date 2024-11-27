package no.nav.familie.pdf.pdf

import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfViewerPreferences
import com.itextpdf.kernel.xmp.XMPMeta
import com.itextpdf.kernel.xmp.XMPMetaFactory
import com.itextpdf.pdfa.PdfADocument

/**
 * `UtilsMetaData` er en hjelpekklasse for å legge til metadata i et PDF-dokument.
 * Vi setter metadataene på to ulike måter for å dekke forskjellige behov:
 * 1. `documentInfo`: Grunnleggende metadata (tittel, skaper) som brukes av PDF-lesere for visning i dokumentegenskaper.
 * 2. `setXmpMetadata`: Avansert, maskinlesbar XMP-metadata for standarder som PDF/UA
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
            }
        return xmpMeta
    }
}
