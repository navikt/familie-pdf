package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.IBlockElement
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.font.FontProvider
import com.itextpdf.layout.font.FontSet
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.fontFamilie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("no.nav.familie.pdf.pdf.visningsvarianter.Html")

fun konverterHtmlString(
    seksjon: Div,
    htmlElement: VerdilisteElement,
): Div {
    val normalizedHtml = normalizeHtmlForPdfUa(htmlElement.label)
    htmlElements(normalizedHtml, createConverterProperties()).forEach { element ->
        when (element) {
            is IBlockElement -> seksjon.add(element)
            is Image -> seksjon.add(element)
            is AreaBreak -> seksjon.add(element)
        }
    }
    return seksjon
}

private fun htmlElements(
    htmlString: String,
    properties: ConverterProperties,
) = HtmlConverter.convertToElements(htmlString, properties)

private fun createFontProvider(): FontProvider {
    val fontProvider = FontProvider()

    // Last inn fontene som bytes. Merk at stien starter med /
    // Dette forutsetter at filene ligger i src/main/resources/fonts/
    val regularBytes = loadFontBytes("/fonts/$fontFamilie-Regular.ttf")
    val boldBytes = loadFontBytes("/fonts/fontFamilie-SemiBold.ttf") // Eller -Bold.ttf
    val italicBytes = loadFontBytes("/fonts/fontFamilie-Italic.ttf")

    // Legg til Regular
    if (regularBytes != null) {
        val font = PdfFontFactory.createFont(regularBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED)
        fontProvider.addFont(regularBytes)
    } else {
        logger.error("Fant ikke Regular font: /fonts/$fontFamilie-Regular.ttf")
    }

    // Legg til Bold / SemiBold
    if (boldBytes != null) {
        val font = PdfFontFactory.createFont(boldBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED)
        fontProvider.addFont(boldBytes)
    }

    // Legg til Italic
    if (italicBytes != null) {
        val font = PdfFontFactory.createFont(italicBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED)
        fontProvider.addFont(italicBytes)
    }

    return fontProvider
}

private fun createConverterProperties(): ConverterProperties =
    ConverterProperties().apply {
        fontProvider = createFontProvider()
    }

private fun loadFontBytes(path: String): ByteArray? =
    try {
        // getResourceAsStream leser filer pakket inni JAR-filen
        object {}.javaClass.getResourceAsStream(path)?.readAllBytes()
    } catch (e: Exception) {
        logger.warn("Kunne ikke laste fontressurs: $path", e)
        null
    }

/**
 * Normaliser HTML før konvertering med iText HtmlConverter.
 * - Wrapper heading-tekst i <span>.
 * - Fjerner <p> inne i heading (flytter blokker ut).
 * - Pakker løse tekstnoder i <p>.
 * - Sørger for at <li> inneholder inline-innhold (teksten i <span>).
 */
fun normalizeHtmlForPdfUa(htmlFragment: String): String {
    // Parse som fragment (body)
    val doc: Document = Jsoup.parseBodyFragment(htmlFragment, "")
    val body = doc.body()

    // 1) Wrap loose text nodes under body/div into <p>
    wrapLooseTextNodes(body)

    // 2) For alle headings: ensure only inline children (wrap text into span) and move block children out
    for (h in 1..6) {
        body.select("h$h").forEach { normalizeHeading(it) }
    }

    // 3) For all li: ensure inline-only children (wrap text nodes in span and unwrap block children)
    body.select("li").forEach { normalizeListItem(it) }

    // 4) For divs: ensure no loose text nodes (wrap them in <p>)
    body.select("div").forEach { wrapLooseTextNodes(it) }

    // 5) Optional: ensure anchors in headings are inline-wrapped
    body.select("h1,h2,h3,h4,h5,h6").forEach { h ->
        h.select("a").forEach { a ->
            // if anchor contains block children, move them out
            a.childNodes().filterIsInstance<Element>().forEach { child ->
                if (isBlockElement(child)) {
                    // move block after heading
                    h.after(child)
                }
            }
        }
    }

    // Return inner HTML (fragment)
    return body.html()
}

/** Wrap alle TextNode direkte under element i <p>, unntatt når text kun whitespace. */
private fun wrapLooseTextNodes(parent: Element) {
    val nodes = parent.childNodes().toList()
    val toWrap = mutableListOf<Node>()
    for (n in nodes) {
        if (n is TextNode) {
            val text = n.text()
            if (text.isNotBlank()) {
                toWrap.add(n)
            } else {
                // trim whitespace text nodes
                n.remove()
            }
        } else if (n is Element && isIgnorableInline(n)) {
            // inline element, can be part of same paragraph: group together
            toWrap.add(n)
        } else {
            // encountered a block element; if we have buffered inline nodes, wrap them
            if (toWrap.isNotEmpty()) {
                val p = Element("p")
                toWrap.forEach { p.appendChild(it) }
                parent.insertChildren(parent.children().indexOf(n), p)
                toWrap.clear()
            }
        }
    }
    if (toWrap.isNotEmpty()) {
        val p = Element("p")
        toWrap.forEach { p.appendChild(it) }
        parent.appendChild(p)
        toWrap.clear()
    }
}

/** Normalize a heading element: ensure it only contains inline children (wrap text in span),
 *  move any block children (e.g. p, div) to after the heading as separate <p> */
private fun normalizeHeading(heading: Element) {
    val children = heading.childNodes().toList()
    val inlineBuffer = mutableListOf<Node>()

    fun flushInlineBuffer() {
        if (inlineBuffer.isEmpty()) return
        // create a single span and move inlineBuffer into it (or append direct inline nodes if they are already inline)
        val span = Element("span")
        inlineBuffer.forEach { span.appendChild(it) }
        heading.appendChild(span)
        inlineBuffer.clear()
    }

    // remove existing children (we'll rebuild)
    heading.empty()

    // re-walk original children, moving block children after heading
    for (c in children) {
        if (c is TextNode) {
            if (c.text().isNotBlank()) inlineBuffer.add(c)
        } else if (c is Element) {
            if (isBlockElement(c)) {
                // block child: flush inlineBuffer as span into heading, then move block after heading
                flushInlineBuffer()
                // move the block element after the heading in the DOM
                heading.after(c)
            } else {
                // inline element: keep in buffer
                inlineBuffer.add(c)
            }
        } else {
            // unknown node: treat as inline text
            inlineBuffer.add(c)
        }
    }
    // finally flush any inline content as span
    flushInlineBuffer()

    // If heading ended up empty (rare), add an empty span to avoid empty heading
    if (heading.childNodeSize() == 0) {
        heading.appendElement("span")
    }
}

/** Ensure LI contains inline only — wrap text/inline children into a span (or keep existing inline) */
private fun normalizeListItem(li: Element) {
    val originalKids = li.childNodes().toList()
    li.empty()

    val inlineSpan = Element("span")
    var hadInline = false

    for (kid in originalKids) {
        when (kid) {
            is TextNode -> {
                if (kid.text().isNotBlank()) {
                    inlineSpan.appendChild(kid)
                    hadInline = true
                }
            }
            is Element -> {
                if (isBlockElement(kid)) {
                    // block child inside LI: move it outside as a paragraph after this LI
                    li.after(kid)
                } else {
                    // inline element (a, strong, em, span, etc.)
                    inlineSpan.appendChild(kid)
                    hadInline = true
                }
            }
            else -> {
                inlineSpan.appendChild(kid)
                hadInline = true
            }
        }
    }

    if (hadInline) {
        li.appendChild(inlineSpan)
    } else {
        // ensure at least one child
        li.appendElement("span")
    }
}

/** Heuristikk for block elements (we treat common ones) */
private fun isBlockElement(el: Element): Boolean {
    val blockTags =
        setOf(
            "div",
            "p",
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
    return el.tagName().lowercase() in blockTags
}

/** Inline that we allow in paragraphs without wrapping them separately */
private fun isIgnorableInline(el: Element): Boolean {
    val inlineTags =
        setOf(
            "span",
            "a",
            "strong",
            "b",
            "em",
            "i",
            "u",
            "small",
            "sub",
            "sup",
            "code",
            "mark",
        )
    return el.tagName().lowercase() in inlineTags
}
