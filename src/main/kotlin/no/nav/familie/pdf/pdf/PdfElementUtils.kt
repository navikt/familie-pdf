package no.nav.familie.pdf.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.pdf.domain.VerdilisteItem

object PdfElementUtils {
    fun navLogoBilde(): Image =
        Image(ImageDataFactory.create(javaClass.getResource("/logo/NAV_logo_digital_Red.png"))).apply {
            setWidth(75f)
            setFixedPosition(460f, 770f, 100f)
            accessibilityProperties.alternateDescription = "NAV logo"
        }

    fun lagVerdiElement(element: VerdilisteItem): Paragraph =
        Paragraph().apply {
            (element.label)
                .takeIf { it?.isNotEmpty() == true }
                ?.let { add(Text(it).apply { simulateBold() }) }
            element.alternativer?.takeIf { it.isNotEmpty() }?.let {
                add(Text("\n"))
                add(
                    Text(it).apply {
                        simulateItalic()
                        setFontSize(10f)
                    },
                )
            }
            add(Text("\n"))
            if (element.label == "Adresse" && element.verdi != null) {
                add(sjekkDobbelLinjeskift(element.verdi))
            } else {
                add(element.verdi)
            }
            setFontSize(12f)
            isKeepTogether = true
            accessibilityProperties.role = StandardRoles.P
        }

    fun sjekkDobbelLinjeskift(tekst: String): String {
        val bareLinjeskiftRegex = Regex("^\\n+$")
        val dobbelLinjeskiftRegex = Regex("\\n{2,}")
        val rensetVerdi =
            if (tekst.isEmpty() || tekst.matches(bareLinjeskiftRegex)) {
                "Ingen registrert adresse"
            } else {
                tekst.replace(dobbelLinjeskiftRegex, "\n")
            }
        return rensetVerdi
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
            simulateBold()
            accessibilityProperties.role = rolle
        }
}
