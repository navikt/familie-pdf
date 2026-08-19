package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.ListItem
import no.nav.familie.pdf.pdf.domain.VerdilisteElement

fun lagPunktliste(
    verdiListe: List<VerdilisteElement>,
    isFirst: Boolean = false,
): com.itextpdf.layout.element.List {
    val list =
        com.itextpdf.layout.element
            .List()
            .apply {
                setMarginLeft(if (isFirst) 0f else 15f)
                StandardRoles.L
                setListSymbol("\u2022 ")
                symbolIndent = 8f
            }
    verdiListe.forEach {
        list.add(ListItem(it.label)).apply {
            StandardRoles.LI
        }
    }
    return list
}
