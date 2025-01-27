package no.nav.familie.pdf.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.List
import com.itextpdf.layout.element.ListItem
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.UnitValue
import no.nav.familie.pdf.pdf.PdfUtils.FontStil
import no.nav.familie.pdf.pdf.PdfUtils.settFont
import no.nav.familie.pdf.pdf.domain.VerdilisteElement

object PdfElementUtils {
    fun navLogoBilde(): Image =
        Image(ImageDataFactory.create(javaClass.getResource("/logo/NAV_logo_digital_Red.png"))).apply {
            setWidth(75f)
            setFixedPosition(460f, 770f, 100f)
            accessibilityProperties.alternateDescription = "NAV logo"
        }

    fun lagVerdiElement(element: VerdilisteElement): Paragraph =
        Paragraph().apply {
            add(Text(leggTilKolon(element.label)).apply { settFont(FontStil.SEMIBOLD) })
            add(Text("\n"))
            add(element.verdi)
            isKeepTogether = true
            accessibilityProperties.role = StandardRoles.P
        }

    // Kun dersom streng ikke har tegn bakerst
    private fun leggTilKolon(tekst: String): String =
        if (tekst.last() !in setOf('?', ':', '.', '!', ';')) {
            "$tekst:"
        } else {
            tekst
        }

    fun lagPunktliste(punkter: kotlin.collections.List<VerdilisteElement>): Div =
        Div().apply {
            punkter.forEach { punkt ->
                add(punktliste().apply { add(ListItem(punkt.label)) })
            }
            isKeepTogether = true
            accessibilityProperties.role = StandardRoles.DIV
        }

    private fun punktliste() =
        List().apply {
            symbolIndent = 8f
            setListSymbol("\u2022")
        }

    fun lagTekstElement(
        tekst: String,
        fontStil: FontStil = FontStil.REGULAR,
    ): Paragraph =
        Paragraph().apply {
            add(Text(tekst))
            settFont(fontStil)
            isKeepTogether = true
            accessibilityProperties.role = StandardRoles.P
        }

    fun lagOverskriftH1(tekst: String): Paragraph = lagOverskrift(tekst, 20f, StandardRoles.H1)

    fun lagOverskriftH2(tekst: String): Paragraph = lagOverskrift(tekst, 16f, StandardRoles.H2)

    fun lagOverskriftH3(tekst: String): Paragraph = lagOverskrift(tekst, 14f, StandardRoles.H3)

    fun lagOverskriftH4(tekst: String): Paragraph = lagOverskrift(tekst, 12f, StandardRoles.H4, false)

    private fun lagOverskrift(
        tekst: String,
        tekstStørrelse: Float,
        rolle: String,
        erFarget: Boolean = true,
    ): Paragraph =
        Paragraph(tekst).apply {
            if (erFarget) setFontColor(DeviceRgb(0, 52, 125))
            setFontSize(tekstStørrelse)
            settFont(FontStil.SEMIBOLD)
            accessibilityProperties.role = rolle
        }

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
        tabell.addCell(lagTabellOverskriftscelle("Spørsmål"))
        tabell.addCell(lagTabellOverskriftscelle("Svar", false))
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
}
