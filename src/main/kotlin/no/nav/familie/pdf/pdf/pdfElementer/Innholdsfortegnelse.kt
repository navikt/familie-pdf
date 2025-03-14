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
import no.nav.familie.pdf.pdf.FontStil
import no.nav.familie.pdf.pdf.PDFdokument
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.hentOversettelse
import no.nav.familie.pdf.pdf.setSkjemanummer
import no.nav.familie.pdf.pdf.settFont

data class InnholdsfortegnelseOppføringer(
    val tittel: String,
    val sideNummer: Int,
)

object Innholdsfortegnelse {
    private fun beregnAntallSider(
        feltMap: FeltMap,
        harInnholdsfortegnelse: Boolean? = null,
    ): Int {
        val midlertidigPdfADokument = PDFdokument.lagPdfADocument(ByteArrayOutputStream())
        Document(midlertidigPdfADokument).apply {
            settFont(FontStil.REGULAR)
            leggTilSeksjoner(feltMap)
            harInnholdsfortegnelse?.let {
                val innholdsfortegnelseTitler = feltMap.verdiliste.map { InnholdsfortegnelseOppføringer(it.label, 1) }
                leggTilInnholdsfortegnelse(feltMap, innholdsfortegnelseTitler)
            }
        }
        return midlertidigPdfADokument.numberOfPages
    }

    private fun beregnAntallSiderInnholdsfortegnelse(feltMap: FeltMap): Int = beregnAntallSider(feltMap, harInnholdsfortegnelse = true) - beregnAntallSider(feltMap)

    fun genererInnholdsfortegnelseOppføringer(feltMap: FeltMap): List<InnholdsfortegnelseOppføringer> {
        val sidetallInnholdsfortegnelse = beregnAntallSiderInnholdsfortegnelse(feltMap)
        val midlertidigPdfADokument = PDFdokument.lagPdfADocument(ByteArrayOutputStream())
        val document = Document(midlertidigPdfADokument).apply { settFont(FontStil.REGULAR) }

        return feltMap.verdiliste.map { seksjon ->
            document.add(lagSeksjon(seksjon))
            InnholdsfortegnelseOppføringer(seksjon.label, midlertidigPdfADokument.numberOfPages + sidetallInnholdsfortegnelse)
        }
    }

    fun Document.leggTilInnholdsfortegnelse(
        feltMap: FeltMap,
        innholdsfortegnelseOppføringer: List<InnholdsfortegnelseOppføringer>,
    ) {
        add(lagOverskriftH1(feltMap.label))
        add(NavLogo.navLogoBilde())
        setSkjemanummer(this, feltMap.skjemanummer)
        val innholdsfortegnelse: String =
            hentOversettelse(
                bokmål = "Innholdsfortegnelse",
                nynorsk = "Innhaldsliste",
                engelsk = "Table of Contents",
            )
        add(lagOverskriftH2(innholdsfortegnelse))
        add(lagInnholdsfortegnelse(innholdsfortegnelseOppføringer))
        add(AreaBreak(AreaBreakType.NEXT_PAGE))
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
