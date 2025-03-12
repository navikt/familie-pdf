package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfString
import com.itextpdf.kernel.pdf.action.PdfAction
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Link
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Tab
import com.itextpdf.layout.element.TabStop
import com.itextpdf.layout.properties.AreaBreakType
import com.itextpdf.layout.properties.TabAlignment
import com.itextpdf.pdfa.PdfADocument
import no.nav.familie.pdf.pdf.FontStil
import no.nav.familie.pdf.pdf.PDFdokument
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.hentOversettelse
import no.nav.familie.pdf.pdf.setSkjemanummer
import no.nav.familie.pdf.pdf.settFont
import org.slf4j.LoggerFactory

data class InnholdsfortegnelseOppføringer(
    val tittel: String,
    val sideNummer: Int,
)

object Innholdsfortegnelse {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun beregnAntallSider(
        feltMap: FeltMap,
        innholdsfortegnelse: MutableList<InnholdsfortegnelseOppføringer>,
    ): Int {
        val midlertidigPdfADokument = PDFdokument.lagPdfADocument(ByteArrayOutputStream())
        Document(midlertidigPdfADokument).apply {
            settFont(FontStil.REGULAR)
            leggTilSeksjoner(
                feltMap,
                innholdsfortegnelse,
                midlertidigPdfADokument,
            )
            val sideAntallFørInnholdsfortegnelse = midlertidigPdfADokument.numberOfPages
            leggTilInnholdsfortegnelse(feltMap.label, innholdsfortegnelse, feltMap.skjemanummer)
            val sideAntallEtterInnholdsfortegnelse = midlertidigPdfADokument.numberOfPages
            close()
            innholdsfortegnelse.clear()
            return sideAntallEtterInnholdsfortegnelse - sideAntallFørInnholdsfortegnelse
        }
    }

    fun Document.leggTilSeksjoner(
        feltMap: FeltMap,
        innholdsfortegnelse: MutableList<InnholdsfortegnelseOppføringer>,
        pdfADokument: PdfADocument,
        sideAntallInnholdsfortegnelse: Int = 0,
    ) {
        val harInnholdsfortegnelse = feltMap.pdfConfig.harInnholdsfortegnelse

        if (harInnholdsfortegnelse) {
            feltMap.verdiliste.forEach { element ->
                element.verdiliste.let {
                    val navigeringDestinasjon = element.label
                    add(lagSeksjon(element, navigeringDestinasjon))
                    innholdsfortegnelse.add(InnholdsfortegnelseOppføringer(element.label, pdfADokument.numberOfPages + sideAntallInnholdsfortegnelse))
                }
            }
        } else {
            leggTilForside(feltMap.label, feltMap.skjemanummer)
            feltMap.verdiliste.forEach { element ->
                element.verdiliste.let {
                    val navigeringDestinasjon = element.label
                    add(lagSeksjon(element, navigeringDestinasjon))
                }
            }
        }
    }

    fun Document.leggTilInnholdsfortegnelse(
        overskrift: String,
        innholdsfortegnelseOppføringer: List<InnholdsfortegnelseOppføringer>,
        skjemanummer: String?,
    ) {
        add(AreaBreak(AreaBreakType.NEXT_PAGE))
        add(lagOverskriftH1(overskrift))
        add(NavLogo.navLogoBilde())
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

    fun Document.leggTilForside(
        overskrift: String,
        skjemanummer: String?,
    ) {
        add(lagOverskriftH1(overskrift))
        add(NavLogo.navLogoBilde())
        setSkjemanummer(this, skjemanummer)
    }

    fun leggInnholdsfortegnelsenFørst(
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

    fun lagInnholdsfortegnelse(innholdsfortegnelseOppføringer: List<InnholdsfortegnelseOppføringer>): Paragraph {
        val innholdsfortegnelseWrapper =
            Paragraph().apply {
                accessibilityProperties.role = StandardRoles.L
            }

        innholdsfortegnelseOppføringer.forEach { innholdsfortegnelseElement ->
            val påSide: String =
                hentOversettelse(
                    bokmål = "på side",
                    nynorsk = "på side",
                    engelsk = "on page",
                )
            val alternativTekst = "${innholdsfortegnelseElement.tittel} $påSide ${innholdsfortegnelseElement.sideNummer}"
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
