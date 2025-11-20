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
import org.jsoup.Jsoup
import org.jsoup.nodes.*

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

/**
 * Generisk HTML-sanitizer for PDF/UA-2 kompatibilitet i iText 9.3.0.
 * Normaliserer alle uforutsette strukturer til en UA-2-kompatibel HTML-form.
 */
fun sanitizeHtmlForPdfUa(htmlFragment: String): String {
    val doc = Jsoup.parseBodyFragment(htmlFragment)
    val body = doc.body()

    sanitizeNode(body)

    return body.html()
}

/**
 * Rekursiv tre-sanitizer.
 */
private fun sanitizeNode(node: Node) {
    if (node is Element) {
        normalizeElement(node)
        // Rekursiv traversering ETTER normalisering
        node.childNodes().toList().forEach { sanitizeNode(it) }
    }
}

/**
 * Normaliserer et HTML-element:
 * - heading: kun inline → SPAN
 * - li: kun inline → block flyttes ut
 * - block children sorteres fra inline
 * - inline nodes pakkes i P eller SPAN
 */
private fun normalizeElement(el: Element) {
    val tag = el.tagName().lowercase()

    when (tag) {
        in headingTags -> normalizeHeading(el)
        "li" -> normalizeListItem(el)
        "a" -> normalizeAnchor(el)
        "p" -> normalizeParagraph(el)
        else -> {
            if (isBlock(tag)) {
                normalizeBlockElement(el)
            } else {
                normalizeInlineElement(el)
            }
        }
    }
}

/** --- HANDLERE --- **/

/** Heading: kun inline-elementer tillatt */
private fun normalizeHeading(h: Element) {
    val newInline = mutableListOf<Node>()
    val toMoveOut = mutableListOf<Node>()

    for (c in h.childNodes().toList()) {
        when (c) {
            is TextNode -> if (c.text().isNotBlank()) newInline.add(c)
            is Element -> {
                if (!isBlock(c.tagName())) {
                    newInline.add(c)
                } else {
                    toMoveOut.add(c)
                }
            }
            else -> newInline.add(c)
        }
    }

    h.empty()

    // pakk inline inn i SPAN
    if (newInline.isNotEmpty()) {
        val span = Element("span")
        newInline.forEach { span.appendChild(it) }
        h.appendChild(span)
    } else {
        h.appendElement("span")
    }

    // flytt block etter heading
    toMoveOut.forEach { h.after(it) }
}

/** LI: kun inline → block flyttes ut */
private fun normalizeListItem(li: Element) {
    val inline = mutableListOf<Node>()
    val toMove = mutableListOf<Node>()

    for (c in li.childNodes().toList()) {
        if (c is Element && isBlock(c.tagName())) {
            toMove.add(c)
        } else {
            inline.add(c)
        }
    }

    li.empty()

    val span = Element("span")
    inline.filter { !(it is TextNode && it.text().isBlank()) }.forEach { span.appendChild(it) }

    if (span.childNodeSize() == 0) {
        span.appendText("")
    }

    li.appendChild(span)

    // flytt block etter LI som <p>
    toMove.forEach { b ->
        val p = Element("p")
        p.appendChild(b)
        li.after(p)
    }
}

/** A-tagger skal ikke inneholde block */
private fun normalizeAnchor(a: Element) {
    val inline = mutableListOf<Node>()
    val toMoveOut = mutableListOf<Node>()

    for (c in a.childNodes().toList()) {
        if (c is Element && isBlock(c.tagName())) toMoveOut.add(c) else inline.add(c)
    }

    a.empty()
    inline.forEach { a.appendChild(it) }

    // block etter lenken
    toMoveOut.forEach { a.after(it) }
}

/** P skal kun inneholde inline */
private fun normalizeParagraph(p: Element) {
    val inline = mutableListOf<Node>()
    val toMoveOut = mutableListOf<Node>()

    for (c in p.childNodes().toList()) {
        if (c is Element && isBlock(c.tagName())) {
            toMoveOut.add(c)
        } else {
            inline.add(c)
        }
    }

    p.empty()

    if (inline.isNotEmpty()) {
        inline.forEach { p.appendChild(it) }
    } else {
        p.appendText("")
    }

    // block flyttes etter P
    toMoveOut.forEach { p.after(it) }
}

/** Generelle block-elementer: inline pakkes automatisk i P */
private fun normalizeBlockElement(el: Element) {
    val inlineBuffer = mutableListOf<Node>()

    val newChildren = mutableListOf<Node>()

    fun flush() {
        if (inlineBuffer.isEmpty()) return
        val p = Element("p")
        inlineBuffer.forEach { p.appendChild(it) }
        newChildren.add(p)
        inlineBuffer.clear()
    }

    for (c in el.childNodes().toList()) {
        if (c is Element && isBlock(c.tagName())) {
            flush()
            newChildren.add(c)
        } else if (c is TextNode && c.text().isBlank()) {
            // ignorer whitespace
        } else {
            inlineBuffer.add(c)
        }
    }

    flush()

    el.empty()
    newChildren.forEach { el.appendChild(it) }
}

/** Inline elementer blir liggende som inline */
private fun normalizeInlineElement(el: Element) {
    // normalt ikke nødvendig å gjøre noe
}

/** --- HJELPEFUNKSJONER --- **/

private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")

private fun isBlock(tag: String): Boolean =
    tag.lowercase() in
        setOf(
            "p",
            "div",
            "ul",
            "ol",
            "li",
            "table",
            "thead",
            "tbody",
            "tr",
            "td",
            "section",
            "article",
            "header",
            "footer",
            "figure",
            "figcaption",
            "blockquote",
            "pre",
            "hr",
        )
