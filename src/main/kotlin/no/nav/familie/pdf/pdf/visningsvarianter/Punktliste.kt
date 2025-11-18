package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.ListItem
import no.nav.familie.pdf.pdf.domain.VerdilisteElement

fun lagPunktliste(verdiListe: List<VerdilisteElement>): com.itextpdf.layout.element.List {
    val list =
        com.itextpdf.layout.element
            .List()
            .apply {
                StandardRoles.L
                setListSymbol("\u2022 ")
                symbolIndent = 8f
                isKeepTogether = true
            }
    verdiListe.forEach {
        list.add(ListItem(it.label)).apply {
            StandardRoles.LI
        }
    }
    return list
}
