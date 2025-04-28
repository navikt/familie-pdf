package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.layout.element.Div
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.domain.VisningsVariant
import no.nav.familie.pdf.pdf.hentOversettelse
import no.nav.familie.pdf.pdf.pdfElementer.håndterRekursivVerdiliste
import no.nav.familie.pdf.pdf.pdfElementer.lagOverskriftH4
import no.nav.familie.pdf.pdf.pdfElementer.lagTekstElement

fun håndterVisningsvariant(
    visningsVariant: String,
    verdilisteElement: VerdilisteElement,
    v2: Boolean,
    seksjon: Div,
) {
    if (verdilisteElement.verdiliste?.isNotEmpty() == true) {
        when (visningsVariant) {
            VisningsVariant.TABELL.toString() -> {
                if (v2) {
                    håndterTabell(verdilisteElement, seksjon)
                } else {
                    håndterTabeller(verdilisteElement.verdiliste, seksjon)
                }
            }

            VisningsVariant.PUNKTLISTE.toString() -> {
                håndterPunktliste(verdilisteElement, seksjon)
            }

            VisningsVariant.VEDLEGG.toString() -> {
                håndterVedlegg(verdilisteElement.verdiliste, seksjon, v2)
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

private fun håndterTabell(
    verdilisteElement: VerdilisteElement,
    seksjon: Div,
) = seksjon.apply { add(lagTabell(verdilisteElement)) }

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
    v2: Boolean,
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
        } ?: håndterRekursivVerdiliste(verdiliste, seksjon, v2)
    }
}
