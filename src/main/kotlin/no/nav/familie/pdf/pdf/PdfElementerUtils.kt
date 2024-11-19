package no.nav.familie.pdf.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text

object PdfElementerUtils {
    fun navLogoBilde(): Image =
        Image(ImageDataFactory.create(javaClass.getResource("/logo/NAV_logo_digital_Red.png"))).apply {
            setWidth(75f)
            setFixedPosition(460f, 770f, 100f)
            accessibilityProperties.alternateDescription = "NAV logo"
        }

    fun lagVerdiElement(element: Map<*, *>): Paragraph =
        Paragraph().apply {
            (element["label"] as? String).takeIf { it?.isNotEmpty() == true }?.let { add(Text(it).apply { setBold() }) }
            add(Text("\n"))
            add(Text(element["verdi"].toString()))
            (element["alternativer"] as? String)?.takeIf { it.isNotEmpty() }?.let {
                add(Text("\n"))
                add(Text(it))
            }
            setFontSize(12f)
            isKeepTogether = true
            accessibilityProperties.role = StandardRoles.P
        }

    fun lagTekstElement(tekst: String): Paragraph =
        Paragraph().apply {
            add(Text(tekst))
            setFontSize(12f)
            accessibilityProperties.role = StandardRoles.P
        }

    fun lagOverskriftH1(tekst: String): Paragraph = lagOverskrift(tekst, 24f, StandardRoles.H1)

    fun lagOverskriftH2(tekst: String): Paragraph = lagOverskrift(tekst, 20f, StandardRoles.H2)

    fun lagOverskriftH3(tekst: String): Paragraph = lagOverskrift(tekst, 16f, StandardRoles.H3)

    private fun lagOverskrift(
        tekst: String,
        tekstStørrelse: Float,
        rolle: String,
    ): Paragraph =
        Paragraph(tekst).apply {
            setFontColor(DeviceRgb(0, 52, 125))
            setFontSize(tekstStørrelse)
            setBold()
            accessibilityProperties.role = rolle
        }
}
