package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.layout.Document
import no.nav.familie.pdf.pdf.setSkjemanummer

fun Document.leggTilForside(
    overskrift: String,
    skjemanummer: String?,
) {
    add(lagOverskriftH1(overskrift))
    add(NavLogo.navLogoBilde())
    setSkjemanummer(this, skjemanummer)
}
