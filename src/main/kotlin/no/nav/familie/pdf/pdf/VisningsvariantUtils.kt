package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Div
import no.nav.familie.pdf.pdf.PdfElementUtils.lagOverskriftH4
import no.nav.familie.pdf.pdf.PdfElementUtils.lagPunktliste
import no.nav.familie.pdf.pdf.PdfElementUtils.lagTabell
import no.nav.familie.pdf.pdf.PdfElementUtils.lagTekstElement
import no.nav.familie.pdf.pdf.PdfUtils.hentOversettelse
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
                    håndterTabeller(verdilisteElement, seksjon)
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
        verdiliste: VerdilisteElement,
        seksjon: Div,
    ) = verdiliste.let { seksjon.apply { add(lagTabell(verdiliste)) } }

    private fun håndterPunktliste(
        verdi: VerdilisteElement,
        seksjon: Div,
    ) {
        if (verdi.verdiliste?.isNotEmpty() == true) {
            seksjon.apply {
                add(lagOverskriftH4(verdi.label).apply { setMarginLeft(30f) })
                add(lagPunktliste(verdi.verdiliste).apply { setMarginLeft(30f) })
            }
        }
    }

    private fun håndterVedlegg(
        verdiliste: List<VerdilisteElement>,
        seksjon: Div,
    ) {
        val ingenVedlegg: String =
            hentOversettelse(
                bokmål = "Ingen vedlegg lastet opp i denne søknaden",
                nynorsk = "Ingen vedlegg lasta opp i denne søknaden",
                engelsk = "No attachments uploaded in this application",
            )
        verdiliste.forEach { vedlegg ->
            vedlegg.verdi?.takeIf { it.isEmpty() }?.let {
                seksjon.apply {
                    add(
                        lagTekstElement(ingenVedlegg).apply {
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
