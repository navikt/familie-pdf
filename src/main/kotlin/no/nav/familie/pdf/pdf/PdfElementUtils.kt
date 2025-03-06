package no.nav.familie.pdf.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
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
}
