package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.pdf.PdfUtils.FontStil
import no.nav.familie.pdf.pdf.PdfUtils.settFont

fun lagTekstElement(
    tekst: String,
    fontStil: FontStil = FontStil.REGULAR,
): Paragraph =
    Paragraph().apply {
        add(Text(tekst))
        settFont(fontStil)
        isKeepTogether = true
        accessibilityProperties.role = StandardRoles.P
    }
