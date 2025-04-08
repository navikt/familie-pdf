package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.LineSeparator
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.visningsvarianter.håndterVisningsvariant

fun lagSeksjon(
    element: VerdilisteElement,
    navigeringDestinasjon: String,
    v2: Boolean,
): Div =
    Div().apply {
        add(
            lagOverskriftH2(element.label).apply {
                setDestination(navigeringDestinasjon)
            },
        )
        if (element.verdiliste != null) {
            if (element.visningsVariant != null) {
                håndterVisningsvariant(element.visningsVariant, element, v2, this)
            } else {
                håndterRekursivVerdiliste(element.verdiliste, this, v2)
            }
        }
        add(LineSeparator(SolidLine().apply { color = DeviceRgb(131, 140, 154) }))
    }

fun håndterRekursivVerdiliste(
    verdiliste: List<VerdilisteElement>,
    seksjon: Div,
    v2: Boolean,
    rekursjonsDybde: Int = 1,
) {
    verdiliste.forEach { element ->
        if (element.label.isNotEmpty()) {
            val marginVenstre = 15f * rekursjonsDybde
            Div().apply {
                isKeepTogether = true
                if (element.visningsVariant != null) {
                    håndterVisningsvariant(
                        element.visningsVariant,
                        element,
                        v2,
                        seksjon,
                    )
                } else if (element.verdiliste != null && element.verdiliste.isNotEmpty()) {
                    seksjon.add(lagOverskriftH3(element.label).apply { setMarginLeft(marginVenstre) })
                    håndterRekursivVerdiliste(element.verdiliste, seksjon, v2, rekursjonsDybde + 1)
                } else if (element.verdi != null) {
                    seksjon.add(lagSpørsmålOgSvar(element).apply { setMarginLeft(marginVenstre) })
                }
            }
        }
    }
}
