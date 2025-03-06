package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.Paragraph
import no.nav.familie.pdf.pdf.PdfUtils.FontStil
import no.nav.familie.pdf.pdf.PdfUtils.settFont

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
