package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.action.PdfAction
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation
import com.itextpdf.kernel.pdf.tagging.StandardRoles
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.IBlockElement
import com.itextpdf.layout.element.ILeafElement
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Link
import com.itextpdf.layout.element.List
import com.itextpdf.layout.element.ListItem
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.ListNumberingType
import no.nav.familie.pdf.pdf.FontStil
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.settFont

fun konverterHtmlString(htmlElement: VerdilisteElement): Div =
    Div().apply {
        htmlElements(htmlElement.label).forEach { element ->
            when (element) {
                is IBlockElement -> add(blockElement(element))
                is Image -> add(element)
                is AreaBreak -> add(element)
            }
        }
        isKeepTogether = true
        accessibilityProperties.role = StandardRoles.DIV
    }

private fun htmlElements(htmlString: String) = HtmlConverter.convertToElements(htmlString, ConverterProperties())

private fun blockElement(blockElement: IBlockElement): IBlockElement =
    when (blockElement) {
        is List -> {
            val listType = blockElement.getProperty<Any>(37)
            val newList = if (listType is ListNumberingType) List(listType) else List()
            blockElement.children.forEach { child ->
                if (child is ListItem) {
                    val newItem = ListItem()
                    child.children.forEach { subChild ->
                        if (subChild is IBlockElement) {
                            newItem.add(blockElement(subChild))
                        } else if (subChild is ILeafElement) {
                            newItem.add(Paragraph().apply { add(textElement(subChild as ILeafElement)) })
                        }
                    }
                    newList.add(newItem)
                }
            }
            newList
        }
        else ->
            Paragraph()
                .apply {
                    if (blockElement.getProperty<String>(95) == "bold") {
                        settFont(FontStil.SEMIBOLD)
                    }
                    val role = getRole(blockElement)
                    if (role != null) {
                        if (role == "h3") {
                            setFontSize(14f)
                        } else if (role == "h4") {
                            setFontSize(13f)
                        }
                    }

                    blockElement.children.forEach { child ->
                        when (child) {
                            is IBlockElement -> add(blockElement(child))
                            is ILeafElement -> add(textElement(child))
                        }
                    }
                }.setMultipliedLeading(1.2f)
    }

private fun getRole(blockElement: IBlockElement): String? {
    when (blockElement) {
        is Div -> return (blockElement).accessibilityProperties?.role
        is Paragraph -> return (blockElement).accessibilityProperties?.role
        else -> return null
    }
}

private fun textElement(txt: ILeafElement): ILeafElement {
    if (txt is Text) {
        val textElement = Text(txt.text)

        // Sjekk om txt er en link
        if (txt.accessibilityProperties.role == StandardRoles.LINK || txt.hasProperty(88)) {
            val annotation = txt.getProperty<Any>(88)
            if (annotation is PdfLinkAnnotation) {
                val uri = annotation.action?.get(PdfName.URI)?.toString()

                if (uri != null) {
                    return Link(txt.text, PdfAction.createURI(uri)).apply {
                        settFont(FontStil.REGULAR)
                        setFontColor(ColorConstants.BLUE)
                        setUnderline()
                    }
                }
            }
        }

        if (txt.hasProperty(95)) textElement.settFont(FontStil.SEMIBOLD)

        return textElement
    }

    return Text("")
}
