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
    seksjon: Div,
) {
    if (visningsVariant == VisningsVariant.HTML.toString()) {
        håndterHtml(verdilisteElement, seksjon)
    } else {
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
    if (verdi.verdiliste?.isNotEmpty() == true) {
        seksjon.apply {
            add(lagOverskriftH4(verdi.label).apply { setMarginLeft(30f) })
            add(lagPunktliste(verdi.verdiliste).apply { setMarginLeft(30f) })
        }
    }
}

private fun håndterHtml(
    verdi: VerdilisteElement,
    seksjon: Div,
) {
    seksjon.apply {
        add(konverterHtmlString(verdi).apply { setMarginLeft(15f) })
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
