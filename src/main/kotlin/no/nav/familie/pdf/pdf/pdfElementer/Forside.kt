package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import no.nav.familie.pdf.pdf.setSkjemanummer

fun Document.leggTilForside(
    overskrift: String,
    skjemanummer: String?,
) {
    add(NavLogo.navLogoBilde())
    add(Paragraph("\n\n").apply { StandardRoles.P })
    add(lagOverskriftH1(overskrift))
    setSkjemanummer(this, skjemanummer)
    add(Paragraph("").apply { StandardRoles.P }.setMultipliedLeading(0.5f))
}
