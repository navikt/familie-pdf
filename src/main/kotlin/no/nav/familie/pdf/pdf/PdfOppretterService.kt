package no.nav.familie.pdf.pdf

import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfAConformanceLevel
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfOutputIntent
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfVersion
import com.itextpdf.kernel.pdf.PdfViewerPreferences
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.kernel.pdf.action.PdfAction
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec.createEmbeddedFileSpec
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.kernel.xmp.XMPMetaFactory
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Link
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Tab
import com.itextpdf.layout.element.TabStop
import com.itextpdf.layout.properties.AreaBreakType
import com.itextpdf.layout.properties.TabAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import com.itextpdf.pdfa.PdfADocument
import no.nav.familie.pdf.pdf.elementer.PdfElementer.lagOverskriftH1
import no.nav.familie.pdf.pdf.elementer.PdfElementer.lagOverskriftH2
import no.nav.familie.pdf.pdf.elementer.PdfElementer.lagOverskriftH3
import no.nav.familie.pdf.pdf.elementer.PdfElementer.lagTekstElement
import no.nav.familie.pdf.pdf.elementer.PdfElementer.lagVerdiElement
import no.nav.familie.pdf.pdf.elementer.PdfElementer.navLogoBilde
import no.nav.familie.pdf.pdf.elementer.lagListeMedAlleBarn
import no.nav.familie.pdf.pdf.elementer.lagTabell

class PdfOppretterService {
    fun lagPdf(feltMap: Map<String, Any>): ByteArray {
        feltMap.values.forEach { value ->
            requireNotNull(value) { "feltMap sitt label eller verdiliste er tom." }
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = pdfADocument(byteArrayOutputStream)
        lagDokumentMedInnholdsfortegnelse(pdfADokument, feltMap)

        return byteArrayOutputStream.toByteArray()
    }

    private fun pdfADocument(byteArrayOutputStream: ByteArrayOutputStream): PdfADocument {
        val pdfWriter =
            PdfWriter(
                byteArrayOutputStream,
                WriterProperties().setPdfVersion(PdfVersion.PDF_2_0),
            )
        val inputStream = javaClass.getResourceAsStream("/colorProfile/sRGB_CS_profile.icm")
        val pdfADokument =
            PdfADocument(
                pdfWriter,
                PdfAConformanceLevel.PDF_A_4,
                PdfOutputIntent("Custom", "", null, "sRGB IEC61966-2.1", inputStream),
            )
        pdfADokument.setTagged()
        pdfADokument.catalog.apply {
            put(PdfName.Lang, PdfString("no")) // TODO dynamisk?
            viewerPreferences = PdfViewerPreferences().setDisplayDocTitle(true)
        }
        pdfADokument.addAssociatedFile(
            "TestFile",
            createEmbeddedFileSpec(
                pdfADokument,
                javaClass.getResourceAsStream("/test.pdf")?.readAllBytes(),
                "This is a description",
                "test.pdf",
                null,
            ),
        )
        val forfatterOgSkaper = "ForfatterOgSkaper"
        // TODO dynamisk
        pdfADokument.documentInfo.apply {
            title = "Tittel"
            author = forfatterOgSkaper
            subject = "Tittel"
            keywords = "Tittel"
            creator = "Tittel"
        }
        val xmpMeta = xmpMeta(forfatterOgSkaper)
        pdfADokument.setXmpMetadata(xmpMeta)

        return pdfADokument
    }

    private fun lagDokumentMedInnholdsfortegnelse(
        pdfADokument: PdfADocument,
        feltMap: Map<String, Any>,
    ) {
        val innholdsfortegnelse = mutableListOf<InnholdsfortegnelseOppføringer>()
        val sideantallInnholdsfortegnelse = kalkulerSideantallInnholdsfortegnelse(feltMap, innholdsfortegnelse)

        Document(pdfADokument).apply {
            setFont(pdfSkrift())
            leggTilSeksjonerOgOppdaterInnholdsfortegnelse(
                feltMap,
                innholdsfortegnelse,
                pdfADokument,
                sideantallInnholdsfortegnelse,
            )
            leggTilForsideMedInnholdsfortegnelse(feltMap["label"].toString(), innholdsfortegnelse)
            leggInnholdsfortegnelsenFørst(sideantallInnholdsfortegnelse, pdfADokument)
            leggTilSidevisning(pdfADokument)
            close()
        }
    }

    private fun kalkulerSideantallInnholdsfortegnelse(
        feltMap: Map<String, Any>,
        innholdsfortegnelse: MutableList<InnholdsfortegnelseOppføringer>,
        sideAntallInnholdsfortegnelse: Int = 0,
    ): Int {
        val midlertidigPdfADokument = pdfADocument(ByteArrayOutputStream())
        Document(midlertidigPdfADokument).apply {
            setFont(pdfSkrift())
            leggTilSeksjonerOgOppdaterInnholdsfortegnelse(
                feltMap,
                innholdsfortegnelse,
                midlertidigPdfADokument,
                sideAntallInnholdsfortegnelse,
            )
            val sideAntallFørInnholdsfortegnelse = midlertidigPdfADokument.numberOfPages
            leggTilForsideMedInnholdsfortegnelse(feltMap["label"].toString(), innholdsfortegnelse)
            val sideAntallEtterInnholdsfortegnelse = midlertidigPdfADokument.numberOfPages
            close()
            innholdsfortegnelse.clear()
            return sideAntallEtterInnholdsfortegnelse - sideAntallFørInnholdsfortegnelse
        }
    }

    private fun pdfSkrift(): PdfFont {
        val skriftSti = "/fonts/SourceSans3-Regular.ttf"
        val skriftProgram = FontProgramFactory.createFont(skriftSti)
        val skrift =
            PdfFontFactory.createFont(
                skriftProgram,
                PdfEncodings.MACROMAN,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED,
            )
        return skrift
    }

    private fun Document.leggTilSeksjonerOgOppdaterInnholdsfortegnelse(
        feltMap: Map<String, Any>,
        innholdsfortegnelse: MutableList<InnholdsfortegnelseOppføringer>,
        pdfADokument: PdfADocument,
        sideAntallInnholdsfortegnelse: Int = 0,
    ) {
        (feltMap["verdiliste"] as? List<*>)?.filterIsInstance<Map<*, *>>()?.forEach { element ->
            (element["verdiliste"] as? List<*>)?.let {
                val navigeringDestinasjon = element["label"].toString()
                add(lagSeksjon(element, navigeringDestinasjon))
                innholdsfortegnelse.add(
                    InnholdsfortegnelseOppføringer(
                        element["label"].toString(),
                        pdfADokument.numberOfPages + sideAntallInnholdsfortegnelse,
                    ),
                )
            }
        }
    }

    private fun lagSeksjon(
        element: Map<*, *>,
        navigeringDestinasjon: String,
    ): Div =
        Div().apply {
            isKeepTogether = true
            add(
                lagOverskriftH2(element["label"].toString()).apply {
                    setDestination(navigeringDestinasjon)
                },
            )
            when (element["type"].toString()) {
                "Tabell Barn" -> {
                    val listeMedAlleBarn = lagListeMedAlleBarn(element["verdiliste"] as List<*>)
                    listeMedAlleBarn.forEachIndexed { index, barn ->
                        val barneIndeksTekst = "Barn " + (index + 1).toString()
                        add(lagTabell(barn, barneIndeksTekst))
                    }
                }

                "Vedlegg" -> {
                    håndterVedlegg(element["verdiliste"] as List<*>, this)
                }

                else -> {
                    håndterRekursivVerdiliste(element["verdiliste"] as List<*>, this)
                }
            }
            add(LineSeparator(SolidLine()))
        }

    private fun håndterVedlegg(
        verdiListe: List<*>,
        seksjon: Div,
    ) {
        verdiListe.filterIsInstance<Map<*, *>>().forEach { vedlegg ->
            val vedleggInnhold = vedlegg["verdi"]
            if (vedleggInnhold == "") {
                seksjon.add(lagTekstElement("Ingen vedlegg lastet opp i denne søknaden").apply { setMarginLeft(15f) })
            } else {
                håndterRekursivVerdiliste(verdiListe, seksjon)
            }
        }
    }

    private fun håndterRekursivVerdiliste(
        verdiliste: List<*>,
        seksjon: Div,
        rekursjonsDybde: Int = 1,
    ) {
        verdiliste.filterIsInstance<Map<*, *>>().forEach { element ->
            val verdilisteBarn = element["verdiliste"] as? List<*>
            val marginVenstre = 15f * rekursjonsDybde
            if (verdilisteBarn != null && verdilisteBarn.isNotEmpty()) {
                seksjon.add(lagOverskriftH3(element["label"].toString()).apply { setMarginLeft(marginVenstre) })
                håndterRekursivVerdiliste(verdilisteBarn, seksjon, rekursjonsDybde + 1)
            } else if (element["verdi"] != null) {
                seksjon.add(lagVerdiElement(element).apply { setMarginLeft(marginVenstre) })
            }
        }
    }

    private fun Document.leggTilForsideMedInnholdsfortegnelse(
        overskrift: String,
        innholdsfortegnelseOppføringer: List<InnholdsfortegnelseOppføringer>,
    ) {
        val tittel = overskrift.substringBefore(" (")
        val søknadstype = overskrift.substringAfter(" (").trimEnd(')')
        add(AreaBreak(AreaBreakType.NEXT_PAGE))
        add(lagOverskriftH1(tittel))
        add(navLogoBilde())
        add(
            Paragraph(søknadstype).apply {
                setMarginTop(-10f)
            },
        )
        add(lagOverskriftH2("Innholdsfortegnelse"))
        add(lagInnholdsfortegnelse(innholdsfortegnelseOppføringer))
    }

    private fun leggInnholdsfortegnelsenFørst(
        sideantallInnholdsfortegnelse: Int,
        pdfADokument: PdfADocument,
    ) {
        repeat(sideantallInnholdsfortegnelse) {
            pdfADokument.movePage(pdfADokument.numberOfPages, 1)
        }
    }

    private fun Document.leggTilSidevisning(pdfADokument: PdfADocument) {
        for (sidetall in 1..pdfADokument.numberOfPages) {
            val bunntekst = Paragraph().add("Side $sidetall av ${pdfADokument.numberOfPages}")
            showTextAligned(bunntekst, 559f, 30f, sidetall, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0f)
        }
    }

    private fun xmpMeta(forfatterOgSkaper: String): com.itextpdf.kernel.xmp.XMPMeta {
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

    data class InnholdsfortegnelseOppføringer(
        val tittel: String,
        val sideNummer: Int,
    )

    private fun lagInnholdsfortegnelse(innholdsfortegnelse: List<InnholdsfortegnelseOppføringer>): Paragraph {
        val innholdsfortegnelseWrapper =
            Paragraph().apply {
                accessibilityProperties.role = StandardRoles.L
            }

        innholdsfortegnelse.forEach { innholdsfortegnelseElement ->
            val alternativTekst =
                "Naviger til ${innholdsfortegnelseElement.tittel} på side ${innholdsfortegnelseElement.sideNummer}"
            val lenke =
                Link(
                    innholdsfortegnelseElement.tittel,
                    PdfAction.createGoTo(innholdsfortegnelseElement.tittel),
                ).apply {
                    accessibilityProperties.alternateDescription = alternativTekst
                    accessibilityProperties.role = StandardRoles.LINK
                    linkAnnotation.contents = PdfString(alternativTekst)
                }
            val lBody =
                Paragraph().apply {
                    setMargin(0f)
                    setPadding(0f)
                    accessibilityProperties.role = StandardRoles.LBODY
                    add(lenke)
                    add(Tab())
                    addTabStops(TabStop(1000f, TabAlignment.RIGHT))
                    add("${innholdsfortegnelseElement.sideNummer}")
                }
            val oppføring =
                Paragraph().apply {
                    accessibilityProperties.role = StandardRoles.LI
                    add(lBody)
                }

            innholdsfortegnelseWrapper.add(oppføring)
        }

        return innholdsfortegnelseWrapper
    }
}
