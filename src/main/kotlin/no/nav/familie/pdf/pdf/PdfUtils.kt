package no.nav.familie.pdf.pdf

import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkKontekst

private fun bestemFont(stil: FontStil): PdfFont {
    val skriftSti =
        when (stil) {
            FontStil.REGULAR -> "/fonts/SourceSans3-Regular.ttf"
            FontStil.SEMIBOLD -> "/fonts/SourceSans3-SemiBold.ttf"
            FontStil.ITALIC -> "/fonts/SourceSans3-Italic.ttf"
        }
    val skriftProgram = FontProgramFactory.createFont(skriftSti)
    return PdfFontFactory.createFont(
        skriftProgram,
        PdfEncodings.MACROMAN,
        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED,
    )
}

fun hentOversettelse(
    bokmål: String,
    nynorsk: String,
    engelsk: String,
): String =
    when (SpråkKontekst.brukSpråk()) {
        "nn" -> nynorsk
        "en" -> engelsk
        else -> bokmål
    }

fun Paragraph.settFont(stil: FontStil) {
    this.setFont(bestemFont(stil))
}

fun Document.settFont(stil: FontStil) {
    this.setFont(bestemFont(stil))
}

fun Text.settFont(stil: FontStil) {
    this.setFont(bestemFont(stil))
}

enum class FontStil {
    REGULAR,
    SEMIBOLD,
    ITALIC,
}

fun setSkjemanummer(
    document: Document,
    skjemanummer: String?,
) {
    if (!skjemanummer.isNullOrEmpty()) {
        document.add(
            Paragraph(skjemanummer).apply {
                setMarginTop(-10f)
            },
        )
    }
}
