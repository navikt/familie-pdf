package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfAConformance
import com.itextpdf.kernel.pdf.PdfOutputIntent
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.PdfVersion
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.kernel.pdf.action.PdfAction
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.kernel.pdf.tagging.StandardRoles
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
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.pdfElementer.NavLogo.navLogoBilde
import no.nav.familie.pdf.pdf.pdfElementer.lagOverskriftH1
import no.nav.familie.pdf.pdf.pdfElementer.lagOverskriftH2
import no.nav.familie.pdf.pdf.pdfElementer.lagOverskriftH3
import no.nav.familie.pdf.pdf.pdfElementer.lagSpørsmålOgSvar
import no.nav.familie.pdf.pdf.visningsvarianter.håndterVisningsvariant
import org.slf4j.LoggerFactory

object Innholdsfortegnelse {
    private val logger = LoggerFactory.getLogger(this::class.java)

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
    ) {
        val harInnholdsfortegnelse = feltMap.pdfConfig.harInnholdsfortegnelse
        val innholdsfortegnelse = mutableListOf<InnholdsfortegnelseOppføringer>()

        val sideantallInnholdsfortegnelse =
            if (harInnholdsfortegnelse) kalkulerSideantallInnholdsfortegnelse(feltMap, innholdsfortegnelse) else 0

        leggtilMetaData(pdfADokument, feltMap)

        Document(pdfADokument).apply {
            settFont(FontStil.REGULAR)
            setMargins(36f, 36f, 44f, 36f)

            leggTilSeksjonerOgOppdaterInnholdsfortegnelse(
                feltMap,
                innholdsfortegnelse,
                pdfADokument,
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

    private fun kalkulerSideantallInnholdsfortegnelse(
        feltMap: FeltMap,
        innholdsfortegnelse: MutableList<InnholdsfortegnelseOppføringer>,
    ): Int {
        val midlertidigPdfADokument = lagPdfADocument(ByteArrayOutputStream())
        Document(midlertidigPdfADokument).apply {
            settFont(FontStil.REGULAR)
            leggTilSeksjonerOgOppdaterInnholdsfortegnelse(
                feltMap,
                innholdsfortegnelse,
                midlertidigPdfADokument,
            )
            val sideAntallFørInnholdsfortegnelse = midlertidigPdfADokument.numberOfPages
            leggTilForsideMedInnholdsfortegnelse(feltMap.label, innholdsfortegnelse, feltMap.skjemanummer)
            val sideAntallEtterInnholdsfortegnelse = midlertidigPdfADokument.numberOfPages
            close()
            innholdsfortegnelse.clear()
            return sideAntallEtterInnholdsfortegnelse - sideAntallFørInnholdsfortegnelse
        }
    }

    private fun Document.leggTilSeksjonerOgOppdaterInnholdsfortegnelse(
        feltMap: FeltMap,
        innholdsfortegnelse: MutableList<InnholdsfortegnelseOppføringer>,
        pdfADokument: PdfADocument,
        sideAntallInnholdsfortegnelse: Int = 0,
    ) {
        val harInnholdsfortegnelse = feltMap.pdfConfig.harInnholdsfortegnelse
        if (!harInnholdsfortegnelse) {
            leggTilForsideOgSeksjonerUtenInnholdsfortegnelse(feltMap.label, feltMap.skjemanummer)
        }
        feltMap.verdiliste.forEach { element ->
            element.verdiliste.let {
                val navigeringDestinasjon = element.label
                add(lagSeksjon(element, navigeringDestinasjon))
                if (harInnholdsfortegnelse) {
                    innholdsfortegnelse.add(
                        InnholdsfortegnelseOppføringer(
                            element.label,
                            pdfADokument.numberOfPages + sideAntallInnholdsfortegnelse,
                        ),
                    )
                }
            }
        }
    }

    private fun lagSeksjon(
        element: VerdilisteElement,
        navigeringDestinasjon: String,
    ): Div =
        Div().apply {
            add(
                lagOverskriftH2(element.label).apply {
                    setDestination(navigeringDestinasjon)
                },
            )
            if (element.verdiliste != null) {
                if (element.visningsVariant != null) {
                    håndterVisningsvariant(element.visningsVariant, element, this)
                } else {
                    håndterRekursivVerdiliste(element.verdiliste, this)
                }
            }
            add(LineSeparator(SolidLine().apply { color = DeviceRgb(131, 140, 154) }))
        }

    fun håndterRekursivVerdiliste(
        verdiliste: List<VerdilisteElement>,
        seksjon: Div,
        rekursjonsDybde: Int = 1,
    ) {
        verdiliste.forEach { element ->
            if (element.label.isNotEmpty()) {
                val marginVenstre = 15f * rekursjonsDybde
                Div().apply {
                    isKeepTogether = true
                    if (element.visningsVariant != null) {
                        håndterVisningsvariant(
                            element.visningsVariant,
                            element,
                            seksjon,
                        )
                    } else if (element.verdiliste != null && element.verdiliste.isNotEmpty()) {
                        seksjon.add(lagOverskriftH3(element.label).apply { setMarginLeft(marginVenstre) })
                        håndterRekursivVerdiliste(element.verdiliste, seksjon, rekursjonsDybde + 1)
                    } else if (element.verdi != null) {
                        seksjon.add(lagSpørsmålOgSvar(element).apply { setMarginLeft(marginVenstre) })
                    }
                }
            }
        }
    }

    private fun Document.leggTilForsideMedInnholdsfortegnelse(
        overskrift: String,
        innholdsfortegnelseOppføringer: List<InnholdsfortegnelseOppføringer>,
        skjemanummer: String?,
    ) {
        add(AreaBreak(AreaBreakType.NEXT_PAGE))
        add(lagOverskriftH1(overskrift))
        add(navLogoBilde())
        setSkjemanummer(this, skjemanummer)
        val innholdsfortegnelse: String =
            hentOversettelse(
                bokmål = "Innholdsfortegnelse",
                nynorsk = "Innhaldsliste",
                engelsk = "Table of Contents",
            )
        add(lagOverskriftH2(innholdsfortegnelse))
        add(lagInnholdsfortegnelse(innholdsfortegnelseOppføringer))
    }

    private fun Document.leggTilForsideOgSeksjonerUtenInnholdsfortegnelse(
        overskrift: String,
        skjemanummer: String?,
    ) {
        add(lagOverskriftH1(overskrift))
        add(navLogoBilde())
        setSkjemanummer(this, skjemanummer)
    }

    private fun leggInnholdsfortegnelsenFørst(
        sideantallInnholdsfortegnelse: Int,
        pdfADokument: PdfADocument,
    ) {
        try {
            repeat(sideantallInnholdsfortegnelse) {
                pdfADokument.movePage(pdfADokument.numberOfPages, 1)
            }
        } catch (e: Exception) {
            logger.error("MovePage feiler fordi det finnes en tom eller nullverdi som ikke blir håndtert i lagPdf.", e)
        }
    }

    private fun Document.leggTilSidevisning(pdfADokument: PdfADocument) {
        for (sidetall in 1..pdfADokument.numberOfPages) {
            val bunntekst =
                Paragraph().add(
                    hentOversettelse(
                        bokmål = "Side $sidetall av ${pdfADokument.numberOfPages}",
                        nynorsk = "Side $sidetall av ${pdfADokument.numberOfPages}",
                        engelsk = "Page $sidetall of ${pdfADokument.numberOfPages}",
                    ),
                )
            showTextAligned(bunntekst, 559f, 30f, sidetall, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0f)
        }
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
            val påSide: String =
                hentOversettelse(
                    bokmål = "på side",
                    nynorsk = "på side",
                    engelsk = "on page",
                )
            val alternativTekst = "${innholdsfortegnelseElement.tittel} $påSide"
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
