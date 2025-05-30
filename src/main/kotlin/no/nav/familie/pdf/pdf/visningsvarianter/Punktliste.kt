package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.ListItem
import no.nav.familie.pdf.pdf.domain.VerdilisteElement

fun lagPunktliste(punkter: List<VerdilisteElement>): Div =
    Div().apply {
        punkter.forEach { punkt ->
            add(punktliste().apply { add(ListItem(punkt.label)) })
        }
        isKeepTogether = true
        accessibilityProperties.role = StandardRoles.DIV
    }

private fun punktliste() =
    com.itextpdf.layout.element.List().apply {
        symbolIndent = 8f
        setListSymbol("\u2022")
    }
