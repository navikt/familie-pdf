package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.pdf.FontStil
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.settFont

fun lagSpørsmålOgSvar(element: VerdilisteElement): List<Paragraph> = listOf(leggTilLabel(element.label), leggTilVerdi(element.verdi))

// Kun dersom streng ikke har tegn bakerst
private fun leggTilKolon(tekst: String): String =
    if (tekst.last() !in setOf('?', ':', '.', '!', ';', ' ')) {
        "$tekst:"
    } else {
        tekst
    }

// Kun dersom streng ikke har tegn bakerst
private fun leggTilLabel(tekst: String): Paragraph =
    Paragraph().apply {
        add(
            Text(leggTilKolon(tekst)).apply {
                settFont(FontStil.SEMIBOLD)
                setMarginBottom(13 * 0.1f)
            },
        )
    }

private fun leggTilVerdi(tekst: String?): Paragraph =
    Paragraph().apply {
        add(
            Text(tekst).apply {
                settFont(FontStil.REGULAR)
                setMarginTop(13 * 0.1f)
            },
        )
    }
