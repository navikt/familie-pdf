package no.nav.familie.pdf.pdf

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue

object TabellUtils {
    fun lagTabell(
        tabellData: List<*>,
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
        elementer: List<*>,
        strengManSkalSplitteTabellPå: String,
    ): List<List<*>> {
        val listeMedAlleElementer = mutableListOf<List<*>>()
        var nåværendeElement = mutableListOf<Map<*, *>>()
        elementer.filterIsInstance<Map<*, *>>().forEachIndexed { index, item ->
            if (item["label"].toString() == strengManSkalSplitteTabellPå && index != 0) {
                listeMedAlleElementer.add(nåværendeElement)
                nåværendeElement = mutableListOf()
            }
            nåværendeElement.add(item)
        }
        listeMedAlleElementer.add(nåværendeElement)
        return listeMedAlleElementer
    }

    private fun lagTabellRekursivt(
        tabellData: List<*>,
        tabell: Table,
    ) {
        tabellData.filterIsInstance<Map<*, *>>().forEach { item ->
            val label = item["label"].toString()
            val value = item["verdi"]?.toString() ?: ""
            when {
                item["verdi"] != null -> {
                    tabell.addCell(lagTabellInformasjonscelle(label, erUthevet = true))
                    tabell.addCell(lagTabellInformasjonscelle(value.ifEmpty { " " }, false))
                }

                item["verdiliste"] != null -> {
                    lagTabellRekursivt(item["verdiliste"] as List<*>, tabell)
                }
            }
        }
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
                },
            ).apply {
                setBorder(Border.NO_BORDER)
                if (erUthevet) simulateBold()
                if (erVenstreKolonne) setPaddingRight(10f) else setPaddingLeft(10f)
                accessibilityProperties.role = StandardRoles.TD
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
        verdiliste: List<*>,
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
