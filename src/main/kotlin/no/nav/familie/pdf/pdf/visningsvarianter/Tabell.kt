package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import no.nav.familie.pdf.pdf.FontStil
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.hentOversettelse
import no.nav.familie.pdf.pdf.pdfElementer.lagTekstElement
import no.nav.familie.pdf.pdf.settFont

fun lagTabell(
    tabellData: VerdilisteElement,
): Table {
    requireNotNull(tabellData.verdiliste) { "VerdilisteElement må ha en verdiliste for å kunne lage en tabell" }

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
                Paragraph(tabellData.label).apply {
                    setFontColor(DeviceRgb(0, 52, 125))
                    setFontSize(14f)
                    settFont(FontStil.SEMIBOLD)
                },
            )
        }
    tabell.caption = captionDiv
    val spørsmål: String =
        hentOversettelse(
            bokmål = "Spørsmål",
            nynorsk = "Spørsmål",
            engelsk = "Questions",
        )

    val svar: String =
        hentOversettelse(
            bokmål = "Svar",
            nynorsk = "Svar",
            engelsk = "Answer",
        )
    tabell.addCell(lagTabellOverskriftscelle(spørsmål))
    tabell.addCell(lagTabellOverskriftscelle(svar, false))
    lagTabellRekursivt(tabellData.verdiliste, tabell)
    return tabell
}

private fun lagTabellOverskriftscelle(
    tekst: String,
    erVenstreKolonne: Boolean = true,
): Cell =
    Cell().apply {
        add(
            Paragraph(tekst).apply {
                setFontColor(DeviceRgb(0, 86, 180))
                setFontSize(14f)
                settFont(FontStil.SEMIBOLD)
            },
        )
        setBorder(Border.NO_BORDER)
        if (erVenstreKolonne) setPaddingRight(10f) else setPaddingLeft(10f)
        accessibilityProperties.role = StandardRoles.TH
    }

private fun lagTabellRekursivt(
    tabellData: kotlin.collections.List<VerdilisteElement>,
    tabell: Table,
    harMørkBakgrunn: Boolean = false,
): Boolean {
    var mørkBakgrunn = harMørkBakgrunn
    tabellData.forEach { element ->
        when {
            element.verdi != null -> {
                val labelCelle = lagTabellInformasjonscelle(element.label, erUthevet = true)
                val verdiCelle = lagTabellInformasjonscelle(element.verdi, false)

                if (mørkBakgrunn) {
                    labelCelle.apply { setBackgroundColor(DeviceRgb(204, 225, 255)) }
                    verdiCelle.apply { setBackgroundColor(DeviceRgb(204, 225, 255)) }
                }
                tabell.addCell(labelCelle)
                tabell.addCell(verdiCelle)

                mørkBakgrunn = !mørkBakgrunn
            }

            element.verdiliste != null -> {
                mørkBakgrunn = lagTabellRekursivt(element.verdiliste, tabell, mørkBakgrunn)
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
    Cell().apply {
        // Legger på non-breaking space for at iText sin movePage-funksjon ikke skal feile
        add(lagTekstElement(tekst.ifEmpty { "\u00A0" }, if (erUthevet) FontStil.SEMIBOLD else FontStil.REGULAR))
        setBorder(Border.NO_BORDER)
        if (erVenstreKolonne) {
            setPaddingRight(10f)
            accessibilityProperties.role = StandardRoles.TH
        } else {
            accessibilityProperties.role = StandardRoles.TD
            setPaddingLeft(10f)
        }
    }
