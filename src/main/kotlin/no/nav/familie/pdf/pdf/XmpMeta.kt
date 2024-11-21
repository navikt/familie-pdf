package no.nav.familie.pdf.pdf

import com.itextpdf.kernel.xmp.XMPMeta
import com.itextpdf.kernel.xmp.XMPMetaFactory

/**
 * `XmpMeta`-objektet gir en hjelpefunksjon for å lage XMP-metadata for et PDF-dokument.
 *
 * Denne metadataen inkluderer informasjon som tittel, skaper, beskrivelse og samsvar med PDF/UA-standarder.
 */
object XmpMeta {
    fun lagXmpMeta(forfatterOgSkaper: String): XMPMeta {
        val xmpMeta =
            XMPMetaFactory.create().apply {
                setProperty(
                    "http://purl.org/dc/elements/1.1/",
                    "dc:title",
                    "Accessible PDF/UA Document",
                )
                setProperty("http://purl.org/dc/elements/1.1/", "dc:creator", forfatterOgSkaper)
                setProperty(
                    "http://purl.org/dc/elements/1.1/",
                    "dc:description",
                    "This PDF complies with PDF/UA",
                )
                setProperty(
                    "http://www.aiim.org/pdfua/ns/id/",
                    "pdfuaid:part",
                    "2",
                ) // "2" for UA-2 and "1" for UA-1
                setProperty(
                    "http://www.aiim.org/pdfua/ns/id/",
                    "pdfuaid:rev",
                    "2024",
                ) // TODO dynamic for creation date of the pdf
            }
        return xmpMeta
    }
}
