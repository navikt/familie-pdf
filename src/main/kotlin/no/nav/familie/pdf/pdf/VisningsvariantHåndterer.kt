package no.nav.familie.pdf.pdf

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import no.nav.familie.pdf.pdf.PdfElementUtils.lagPunktliste
import no.nav.familie.pdf.pdf.PdfElementUtils.lagTekstElement
import no.nav.familie.pdf.pdf.PdfUtils.håndterRekursivVerdiliste
import no.nav.familie.pdf.pdf.domain.VerdilisteElement

object VisningsvariantHåndterer {
    fun håndterTabeller(
        verdilisteElement: VerdilisteElement,
        seksjon: Div,
    ) = verdilisteElement.verdiliste?.forEach { verdilisteElement ->
        verdilisteElement.verdiliste?.let { seksjon.add(lagTabell(verdilisteElement)) }
    }

    fun håndterPunktliste(
        verdi: VerdilisteElement,
        seksjon: Div,
    ) {
        seksjon.apply {
            add(lagPunktliste(verdi))
        }
    }

    fun håndterVedlegg(
        verdilisteElement: VerdilisteElement,
        seksjon: Div,
    ) {
        verdilisteElement.verdiliste?.forEach { vedlegg ->

            vedlegg.verdi?.takeIf { it.isEmpty() }?.let {
                seksjon.add(lagTekstElement("Ingen vedlegg lastet opp i denne søknaden").apply { setMarginLeft(15f) })
            } ?: håndterRekursivVerdiliste(verdilisteElement.verdiliste, seksjon)
        }
    }

    private fun lagTabell(
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
                        simulateBold()
                    },
                )
            }
        tabell.caption = captionDiv
        tabell.addCell(lagTabellOverskriftscelle("Spørsmål"))
        tabell.addCell(lagTabellOverskriftscelle("Svar", false))
        tabell.lagTabellRekursivt(tabellData.verdiliste)
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
                    simulateBold()
                },
            )
            setBorder(Border.NO_BORDER)
            if (erVenstreKolonne) setPaddingRight(10f) else setPaddingLeft(10f)
            accessibilityProperties.role = StandardRoles.TH
        }

    private fun Table.lagTabellRekursivt(
        tabellData: List<VerdilisteElement>,
    ) {
        tabellData.forEach { element ->
            when {
                element.verdi != null -> {
                    this.addCell(lagTabellInformasjonscelle(element.label, erUthevet = true))
                    this.addCell(lagTabellInformasjonscelle(element.verdi, erVenstreKolonne = false))
                }
                element.verdiliste != null -> lagTabellRekursivt(element.verdiliste)
            }
        }
    }

    private fun lagTabellInformasjonscelle(
        tekst: String,
        erVenstreKolonne: Boolean = true,
        erUthevet: Boolean = false,
    ): Cell =
        Cell().apply {
            add(Paragraph(tekst).setFontSize(12f))
            setBorder(Border.NO_BORDER)
            if (erUthevet) simulateBold()
            if (erVenstreKolonne) setPaddingRight(10f) else setPaddingLeft(10f)
            accessibilityProperties.role = StandardRoles.TD
        }
}
