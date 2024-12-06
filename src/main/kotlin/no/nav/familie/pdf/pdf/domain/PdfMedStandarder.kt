package no.nav.familie.pdf.pdf.domain

import com.fasterxml.jackson.annotation.JsonValue

data class PdfMedStandarder(
    val pdf: ByteArray,
    val standarder: Map<PdfStandard, Standard>,
)

data class Standard(
    val samsvarer: Boolean,
    val feiletRegel: String,
)

enum class PdfStandard(
    @get:JsonValue
    val standard: String,
) {
    UA1("ua1"),
    UA2("ua2"),
    ONE_A("1a"),
    ONE_B("1b"),
    TWO_A("2a"),
    TWO_B("2b"),
    TWO_U("2u"),
    THREE_A("3a"),
    THREE_B("3b"),
    THREE_U("3u"),
    FOUR("4"),
    FOUR_F("4f"),
    FOUR_E("4e"),
}
