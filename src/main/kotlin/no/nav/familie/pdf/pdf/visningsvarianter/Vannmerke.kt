package no.nav.familie.pdf.pdf.visningsvarianter

import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun addWatermarkToPdf(
    pdfBytes: ByteArray,
    watermarkText: String,
): ByteArray {
    val outputStream = ByteArrayOutputStream()

    PdfReader(ByteArrayInputStream(pdfBytes)).use { reader ->
        PdfWriter(outputStream).use { writer ->
            val pdfDoc = PdfDocument(reader, writer)

            val numberOfPages = pdfDoc.numberOfPages
            for (i in 1..numberOfPages) {
                val page = pdfDoc.getPage(i)
                val pageSize: Rectangle = page.pageSize

                val pdfCanvas = PdfCanvas(page.newContentStreamAfter(), page.resources, pdfDoc)

                pdfCanvas.saveState()
                pdfCanvas.setExtGState(PdfExtGState().apply { fillOpacity = 0.3f })

                val canvas = Canvas(pdfCanvas, pageSize)
                canvas.showTextAligned(
                    Paragraph(watermarkText)
                        .setFontSize(60f)
                        .setFontColor(ColorConstants.RED),
                    pageSize.width / 2,
                    pageSize.height / 2,
                    i,
                    TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE,
                    Math.toRadians(45.0).toFloat(), // rotation angle
                )

                canvas.close()
                pdfCanvas.restoreState()
            }

            pdfDoc.close()
        }
    }

    return outputStream.toByteArray()
}
