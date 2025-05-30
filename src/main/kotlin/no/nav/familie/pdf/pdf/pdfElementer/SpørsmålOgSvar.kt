package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.kernel.pdf.tagging.StandardRoles
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
        setMarginBottom(13 * 0.1f)
        isKeepTogether = true
        accessibilityProperties.role = StandardRoles.P
        add(
            Text(leggTilKolon(tekst)).apply {
                settFont(FontStil.SEMIBOLD)
            },
        )
    }

private fun leggTilVerdi(tekst: String?): Paragraph =
    Paragraph().apply {
        setMarginTop(13 * 0.1f)
        isKeepTogether = true
        accessibilityProperties.role = StandardRoles.P
        add(
            Text(tekst).apply {
                settFont(FontStil.REGULAR)
            },
        )
    }

/*
fun lagSpørsmålOgSvar(element: VerdilisteElement): List<Paragraph> =
    listOf(
        Paragraph().apply {
            add(Text(leggTilKolon(element.label)).apply { settFont(FontStil.SEMIBOLD) })
            add(Text("\n"))
            add(element.verdi)
            isKeepTogether = true
            accessibilityProperties.role = StandardRoles.P
        },
        Paragraph("\n").apply {
            isKeepTogether = true
            accessibilityProperties.role = StandardRoles.P
        },
    )
*/
