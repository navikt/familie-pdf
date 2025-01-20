package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Div
import no.nav.familie.pdf.pdf.PdfElementUtils.lagOverskriftH3
import no.nav.familie.pdf.pdf.PdfElementUtils.lagPunktliste
import no.nav.familie.pdf.pdf.PdfElementUtils.lagTabell
import no.nav.familie.pdf.pdf.PdfElementUtils.lagTekstElement
import no.nav.familie.pdf.pdf.PdfUtils.håndterRekursivVerdiliste
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.domain.VisningsVariant

object VisningsvariantUtils {
    fun håndterVisningsvariant(
        visningsVariant: String,
        verdilisteElement: VerdilisteElement,
        seksjon: Div,
    ) {
        if (verdilisteElement.verdiliste?.isNotEmpty() == true) {
            when (visningsVariant) {
                VisningsVariant.TABELL.toString() -> {
                    håndterTabeller(verdilisteElement.verdiliste, seksjon)
                }

                VisningsVariant.PUNKTLISTE.toString() -> {
                    håndterPunktliste(verdilisteElement, seksjon)
                }

                VisningsVariant.VEDLEGG.toString() -> {
                    håndterVedlegg(verdilisteElement.verdiliste, seksjon)
                }
            }
        }
    }

    private fun håndterTabeller(
        verdiliste: List<VerdilisteElement>,
        seksjon: Div,
    ) = verdiliste.forEach { verdilisteElement ->
        verdiliste.let { seksjon.apply { add(lagTabell(verdilisteElement)) } }
    }

    private fun håndterPunktliste(
        verdi: VerdilisteElement,
        seksjon: Div,
    ) {
        seksjon.apply {
            add(lagOverskriftH3(verdi.label))
            add(lagPunktliste(verdi))
        }
    }

    private fun håndterVedlegg(
        verdiliste: List<VerdilisteElement>,
        seksjon: Div,
    ) {
        verdiliste.forEach { vedlegg ->
            vedlegg.verdi?.takeIf { it.isEmpty() }?.let {
                seksjon.apply {
                    add(
                        lagTekstElement("Ingen vedlegg lastet opp i denne søknaden").apply {
                            setMarginLeft(
                                15f,
                            )
                        },
                    )
                }
            } ?: håndterRekursivVerdiliste(verdiliste, seksjon)
        }
    }
}
