package no.nav.familie.pdf.pdf.types

import no.nav.familie.pdf.pdf.validerPdf

data class PdfMedStandarder(
    val pdf: ByteArray,
    val standarder: Standarder,
)

fun lagPdfMedStandarder(pdf: ByteArray): PdfMedStandarder =
    PdfMedStandarder(
        pdf,
        Standarder(
            ua1 = validerPdf(pdf, "ua1"),
            ua2 = validerPdf(pdf, "ua2"),
            `1a` = validerPdf(pdf, "1a"),
            `1b` = validerPdf(pdf, "1b"),
            `2a` = validerPdf(pdf, "2a"),
            `2b` = validerPdf(pdf, "2b"),
            `2u` = validerPdf(pdf, "2u"),
            `3a` = validerPdf(pdf, "3a"),
            `3b` = validerPdf(pdf, "3b"),
            `3u` = validerPdf(pdf, "3u"),
            `4` = validerPdf(pdf, "4"),
            `4f` = validerPdf(pdf, "4f"),
            `4e` = validerPdf(pdf, "4e"),
        ),
    )
