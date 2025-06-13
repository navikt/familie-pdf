package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.LineSeparator
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.visningsvarianter.håndterVisningsvariant

fun lagSeksjon(
    element: VerdilisteElement,
    v2: Boolean,
): Div =
    Div().apply {
        add(
            lagOverskriftH2(element.label).apply {
                setDestination(element.label)
            },
        )
        if (element.verdiliste != null) {
            if (element.visningsVariant != null) {
                håndterVisningsvariant(element.visningsVariant, element, v2, this)
            } else {
                håndterRekursivVerdiliste(element.verdiliste, this, v2)
            }
        }
        add(LineSeparator(DashedLine().apply { color = DeviceRgb(131, 140, 154) }))
    }

fun håndterRekursivVerdiliste(
    verdiliste: List<VerdilisteElement>,
    seksjon: Div,
    v2: Boolean,
    rekursjonsDybde: Int = 1,
) {
    verdiliste.forEach { element ->
        if (!element.label.isNullOrBlank()) {
            val marginVenstre = 15f * rekursjonsDybde
            Div().apply {
                isKeepTogether = true
                accessibilityProperties.role = com.itextpdf.kernel.pdf.tagging.StandardRoles.DIV
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
                    val spmOgSvar = lagSpørsmålOgSvar(element)
                    val spørsmål = spmOgSvar[0].apply { setMarginLeft(marginVenstre) }
                    seksjon.add(spørsmål)
                    if (spmOgSvar.size > 1) {
                        seksjon.add(spmOgSvar[1].apply { setMarginLeft(marginVenstre) })
                    }
                }
            }
        }
    }
}

fun Document.leggTilSeksjoner(
    feltMap: FeltMap,
    v2: Boolean,
) {
    feltMap.verdiliste.forEach {
        add(lagSeksjon(it, v2))
    }
}
