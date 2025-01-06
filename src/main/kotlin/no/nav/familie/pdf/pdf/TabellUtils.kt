package no.nav.familie.pdf.pdf

import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import no.nav.familie.pdf.pdf.domain.VerdilisteElement

object TabellUtils {
    fun lagTabell(
        tabellData: List<VerdilisteElement>,
        caption: String,
    ): Table {
        val tabell =
            Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).apply {
                useAllAvailableWidth()
                setMarginBottom(10f)
                setMarginLeft(15f)
                accessibilityProperties.role = StandardRoles.TABLE
            }
        val captionDiv =
            Div().apply {
                add(
                    Paragraph(caption).apply {
                        setFontColor(DeviceRgb(0, 52, 125))
                        setFontSize(14f)
                        simulateBold()
                    },
                )
            }
        tabell.caption = captionDiv

        tabell.addCell(lagTabellOverskriftscelle("Spørsmål"))
        tabell.addCell(lagTabellOverskriftscelle("Svar", false))
        lagTabellRekursivt(tabellData, tabell)
        return tabell
    }

    private fun lagListeMedAlleElementer(
        elementer: List<VerdilisteElement>,
        strengManSkalSplitteTabellPå: String,
    ): List<List<VerdilisteElement>> {
        val listeMedAlleElementer = mutableListOf<List<VerdilisteElement>>()
        var nåværendeElement = mutableListOf<VerdilisteElement>()
        elementer.forEachIndexed { index, item ->
            if (item.label == strengManSkalSplitteTabellPå && index != 0) {
                listeMedAlleElementer.add(nåværendeElement)
                nåværendeElement = mutableListOf()
            }
            nåværendeElement.add(item)
        }
        listeMedAlleElementer.add(nåværendeElement)
        return listeMedAlleElementer
    }

    private fun lagTabellRekursivt(
        tabellData: List<VerdilisteElement>,
        tabell: Table,
        bakgrunnErMørk: Boolean = false,
    ): Boolean {
        var mørkBakgrunn = bakgrunnErMørk

        tabellData.forEach { item ->
            val label = item.label
            val value = item.verdi ?: ""
            when {
                item.verdi != null -> {
                    val labelCelle = lagTabellInformasjonscelle(label, erUthevet = true)
                    val verdiCelle = lagTabellInformasjonscelle(value, false)

                    if (mørkBakgrunn) {
                        labelCelle.apply { setBackgroundColor(DeviceRgb(204, 225, 255)) }
                        verdiCelle.apply { setBackgroundColor(DeviceRgb(204, 225, 255)) }
                    }
                    tabell.addCell(labelCelle)
                    tabell.addCell(verdiCelle)

                    mørkBakgrunn = !mørkBakgrunn
                }

                item.verdiliste != null -> {
                    mørkBakgrunn = lagTabellRekursivt(item.verdiliste, tabell, mørkBakgrunn)
                }
            }
        }
        return mørkBakgrunn
    }

    private fun lagTabellInformasjonscelle(
        tekst: String,
        erVenstreKolonne: Boolean = true,
        erUthevet: Boolean = false,
    ): Cell =
        Cell()
            .add(
                Paragraph(tekst).apply {
                    setFontSize(12f)
                    if (erUthevet) settFetSkrift()
                },
            ).apply {
                setBorder(Border.NO_BORDER)
//                if (erUthevet) simulateBold()
                if (erVenstreKolonne) setPaddingRight(10f) else setPaddingLeft(10f)
                accessibilityProperties.role = StandardRoles.TD
            }

    fun Paragraph.settFetSkrift() {
        val skriftSti = "/fonts/SourceSans3-SemiBold.ttf"
        val skriftProgram = FontProgramFactory.createFont(skriftSti)
        val fetFont =
            PdfFontFactory.createFont(
                skriftProgram,
                PdfEncodings.MACROMAN,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED,
            )
        this.apply { setFont(fetFont) }
    }

    private fun lagTabellOverskriftscelle(
        tekst: String,
        erVenstreKolonne: Boolean = true,
    ): Cell =
        Cell()
            .add(
                Paragraph(tekst).apply {
                    setFontColor(DeviceRgb(0, 86, 180))
                    setFontSize(14f)
                    simulateBold()
                },
            ).apply {
                setBorder(Border.NO_BORDER)
                if (erVenstreKolonne) setPaddingRight(10f) else setPaddingLeft(10f)
                accessibilityProperties.role = StandardRoles.TH
            }

    fun håndterTabellBasertPåVisningsvariant(
        verdiliste: List<VerdilisteElement>,
        strengManSkalSplitteTabellPå: String,
        prefiks: String,
        seksjon: Div,
    ) {
        val listeMedAlleBarn = lagListeMedAlleElementer(verdiliste, strengManSkalSplitteTabellPå)
        listeMedAlleBarn.forEachIndexed { index, barn ->
            val barneIndeksTekst = "$prefiks ${index + 1}"
            seksjon.add(lagTabell(barn, barneIndeksTekst))
        }
    }
}
