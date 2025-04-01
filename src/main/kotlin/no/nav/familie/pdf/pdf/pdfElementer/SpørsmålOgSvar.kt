package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.pdf.FontStil
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.settFont

fun lagSpørsmålOgSvar(element: VerdilisteElement): Paragraph =
    Paragraph().apply {
        add(Text(leggTilKolon(element.label)).apply { settFont(FontStil.SEMIBOLD) })
        add(Text("\n"))
        add(element.verdi)
        isKeepTogether = true
        accessibilityProperties.role = StandardRoles.P
    }

// Kun dersom streng ikke har tegn bakerst
private fun leggTilKolon(tekst: String): String =
    if (tekst.last() !in setOf('?', ':', '.', '!', ';', ' ')) {
        "$tekst:"
    } else {
        tekst
    }
