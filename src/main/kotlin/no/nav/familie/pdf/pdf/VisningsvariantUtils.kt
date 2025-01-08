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
        verdiElement: VerdilisteElement,
        seksjon: Div,
    ) {
        when (visningsVariant) {
            VisningsVariant.TABELL_BARN.toString() -> {
                håndterTabeller(verdiElement, "Navn", "Barn", seksjon)
            }

            VisningsVariant.TABELL_ARBEIDSFORHOLD.toString() -> {
                håndterTabeller(verdiElement, "Navn på arbeidssted", "Arbeidsforhold", seksjon)
            }

            VisningsVariant.PUNKTLISTE.toString() -> {
                håndterPunktliste(verdiElement, seksjon)
            }

            VisningsVariant.VEDLEGG.toString() -> {
                håndterVedlegg(verdiElement, seksjon)
            }
        }
    }

    private fun håndterTabeller(
        verdilisteElement: VerdilisteElement,
        strengManSkalSplitteTabellPå: String,
        prefiks: String,
        seksjon: Div,
    ) {
        val listeMedAlleBarn =
            verdilisteElement.verdiliste?.let { lagListeMedAlleElementer(it, strengManSkalSplitteTabellPå) }
        listeMedAlleBarn?.forEachIndexed { index, barn ->
            val barneIndeksTekst = "$prefiks ${index + 1}"
            seksjon.apply { add(lagTabell(barn, barneIndeksTekst)) }
        }
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
        verdilisteElement.verdiliste?.forEach { vedlegg ->
            val vedleggInnhold = vedlegg.verdi
            if (vedleggInnhold == "") {
                seksjon.apply { add(lagTekstElement("Ingen vedlegg lastet opp i denne søknaden").apply { setMarginLeft(15f) }) }
            } else {
                håndterRekursivVerdiliste(verdilisteElement.verdiliste, seksjon)
            }
        }
    }

    private fun lagListeMedAlleElementer(
        elementer: List<VerdilisteElement>,
        strengManSkalSplitteTabellPå: String,
    ): List<List<VerdilisteElement>> {
        val listeMedAlleElementer = mutableListOf<List<VerdilisteElement>>()
        var nåværendeElement = mutableListOf<VerdilisteElement>()
        elementer.forEachIndexed { index, item ->
            if (item.label == strengManSkalSplitteTabellPå && index != 0) {
                listeMedAlleElementer.add(nåværendeElement)
                nåværendeElement = mutableListOf()
            }
            nåværendeElement.add(item)
        }
        listeMedAlleElementer.add(nåværendeElement)
        return listeMedAlleElementer
    }
}
