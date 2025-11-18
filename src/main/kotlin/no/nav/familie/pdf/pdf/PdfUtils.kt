package no.nav.familie.pdf.pdf

import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.tagging.PdfStructElem
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import com.itextpdf.pdfa.PdfADocument
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkKontekst

enum class FontStil {
    REGULAR,
    SEMIBOLD,
    ITALIC,
}

val fontFamilie = "SourceSansPro"

fun Paragraph.settFont(stil: FontStil) {
    this.setFont(bestemFont(stil))
}

fun Document.settFont(stil: FontStil) {
    this.setFont(bestemFont(stil))
}

fun Text.settFont(stil: FontStil) {
    this.setFont(bestemFont(stil))
}

fun bestemFont(stil: FontStil): PdfFont {
    val skriftSti =
        when (stil) {
            FontStil.REGULAR -> "/fonts/$fontFamilie-Regular.ttf"
            FontStil.SEMIBOLD -> "/fonts/$fontFamilie-SemiBold.ttf"
            FontStil.ITALIC -> "/fonts/$fontFamilie-Italic.ttf"
        }
    val skriftProgram = FontProgramFactory.createFont(skriftSti)
    return PdfFontFactory.createFont(
        skriftProgram,
        PdfEncodings.MACROMAN,
        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED,
    )
}

fun Document.leggTilBunntekst(
    pdfADokument: PdfADocument,
    feltMap: FeltMap,
) {
    val tekstStørrelse = 11f
    for (sidetall in 1..pdfADokument.numberOfPages) {
        val page = pdfADokument.getPage(sidetall)
        bunntekstForPage(feltMap, page, sidetall, pdfADokument.numberOfPages, tekstStørrelse)
    }
}

fun Document.bunntekstForPage(
    feltMap: FeltMap,
    page: PdfPage,
    sidetall: Int,
    numberOfPages: Int,
    tekstStørrelse: Float,
) {
    val canvas = PdfCanvas(page)
    canvas.beginMarkedContent(PdfName.Artifact)
    canvas
        .setLineWidth(0.5f)
        .setStrokeColor(DeviceRgb(131, 140, 154))
        .moveTo(38.0, 65.0)
        .lineTo(page.pageSize.width - 38.0, 65.0)
        .stroke()
    canvas.endMarkedContent()

    // Legg til teksten i bunnteksten
    val sidevisningsTekst =
        Paragraph()
            .apply {
                setFontSize(tekstStørrelse)
                StandardRoles.P
            }.add(
                hentOversettelse(
                    bokmål = "Side $sidetall av $numberOfPages",
                    nynorsk = "Side $sidetall av $numberOfPages",
                    engelsk = "Page $sidetall of $numberOfPages",
                ),
            )
    if (feltMap.bunntekst != null) {
        if (feltMap.bunntekst.upperleft != null) {
            showTextAligned(
                Paragraph()
                    .apply {
                        setFontSize(tekstStørrelse)
                        StandardRoles.P
                    }.add(feltMap.bunntekst.upperleft),
                38f,
                48f,
                sidetall,
                TextAlignment.LEFT,
                VerticalAlignment.BOTTOM,
                0f,
            )
        }
        if (feltMap.bunntekst.upperMiddle != null) {
            showTextAligned(
                Paragraph()
                    .apply {
                        setFontSize(tekstStørrelse)
                        StandardRoles.P
                    }.add(feltMap.bunntekst.upperMiddle),
                330f,
                48f,
                sidetall,
                TextAlignment.CENTER,
                VerticalAlignment.BOTTOM,
                0f,
            )
        }
        if (feltMap.bunntekst.upperRight != null) {
            showTextAligned(
                Paragraph()
                    .apply {
                        setFontSize(tekstStørrelse)
                        StandardRoles.P
                    }.add(feltMap.bunntekst.upperRight),
                559f,
                48f,
                sidetall,
                TextAlignment.RIGHT,
                VerticalAlignment.BOTTOM,
                0f,
            )
        }
        if (feltMap.bunntekst.lowerleft != null) {
            showTextAligned(
                Paragraph()
                    .apply {
                        setFontSize(tekstStørrelse)
                        StandardRoles.P
                    }.add(feltMap.bunntekst.lowerleft),
                38f,
                30f,
                sidetall,
                TextAlignment.LEFT,
                VerticalAlignment.BOTTOM,
                0f,
            )
        }
        if (feltMap.bunntekst.lowerMiddle != null) {
            showTextAligned(
                Paragraph()
                    .apply {
                        setFontSize(tekstStørrelse)
                        StandardRoles.P
                    }.add(feltMap.bunntekst.lowerMiddle),
                330f,
                30f,
                sidetall,
                TextAlignment.CENTER,
                VerticalAlignment.BOTTOM,
                0f,
            )
        }
    }
    showTextAligned(sidevisningsTekst, 559f, 30f, sidetall, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0f)
}

fun setSkjemanummer(
    document: Document,
    skjemanummer: String?,
) {
    if (!skjemanummer.isNullOrEmpty()) {
        document.add(
            Paragraph(skjemanummer).apply {
                setMarginTop(-10f)
                StandardRoles.P
            },
        )
    }
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

fun fixHeadingParagraphStructure(pdf: PdfDocument) {
    val root = pdf.structTreeRoot ?: return

    fun recurse(elem: PdfStructElem) {
        val role = elem.role

        val roleValue = role.value
        // Er dette en heading (H1–H6)?
        if (roleValue == StandardRoles.H1 ||
            roleValue == StandardRoles.H2 ||
            roleValue == StandardRoles.H3 ||
            roleValue == StandardRoles.H4 ||
            roleValue == StandardRoles.H5 ||
            roleValue == StandardRoles.H6
        ) {
            fixHeading(elem)
        }

        // Gå videre nedover strukturen
        elem.kids?.forEach { kid ->
            if (kid is PdfStructElem) recurse(kid)
        }
    }

    // Start rekursjon fra rotnoder
    root.kids?.forEach {
        if (it is PdfStructElem) recurse(it)
    }
}

private fun fixHeading(heading: PdfStructElem) {
    val newKids = mutableListOf<Any?>()
    val toRemove = mutableListOf<PdfStructElem>()

    heading.kids?.forEach { child ->
        if (child is PdfStructElem && child.role.value == StandardRoles.P) {
            // Flytt P's barn direkte inn i headingen
            child.kids?.forEach { subKid ->
                newKids.add(subKid)
            }
            toRemove.add(child)
        } else {
            newKids.add(child)
        }
    }

    // Fjern ulovlige P-elementer
    toRemove.forEach { heading.removeKid(it) }

    // Legg inn nye barn (inline)
    newKids.forEach { if (it is PdfStructElem) heading.addKid(it) }
}
