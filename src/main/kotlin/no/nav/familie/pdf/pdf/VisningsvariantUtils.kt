package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Div
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
        when (visningsVariant) {
            VisningsVariant.TABELL.toString() -> {
                håndterTabeller(verdilisteElement, seksjon)
            }
            VisningsVariant.PUNKTLISTE.toString() -> {
                håndterPunktliste(verdilisteElement, seksjon)
            }
            VisningsVariant.VEDLEGG.toString() -> {
                håndterVedlegg(verdilisteElement, seksjon)
            }
        }
    }

    private fun håndterTabeller(
        verdilisteElement: VerdilisteElement,
        seksjon: Div,
    ) = verdilisteElement.verdiliste?.forEach { verdilisteElement ->
        verdilisteElement.verdiliste?.let { seksjon.apply { add(lagTabell(verdilisteElement)) } }
    }

    private fun håndterPunktliste(
        verdi: VerdilisteElement,
        seksjon: Div,
    ) {
        seksjon.apply {
            add(lagPunktliste(verdi))
        }
    }

    private fun håndterVedlegg(
        verdilisteElement: VerdilisteElement,
        seksjon: Div,
    ) {
        val ingenVedlegg: String =
            when (PdfElementUtils.brukSpråk()) {
                "nn" -> "Ingen vedlegg lasta opp i denne søknaden"
                "en" -> "No attachments uploaded in this application"
                else -> "Ingen vedlegg lastet opp i denne søknaden"
            }
        verdilisteElement.verdiliste?.forEach { vedlegg ->
            vedlegg.verdi?.takeIf { it.isEmpty() }?.let {
                seksjon.apply { add(lagTekstElement(ingenVedlegg).apply { setMarginLeft(15f) }) }
            } ?: håndterRekursivVerdiliste(verdilisteElement.verdiliste, seksjon)
        }
    }
}
