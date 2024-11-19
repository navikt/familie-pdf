package no.nav.familie.pdf.pdf.domain

data class PdfMedStandarder(
    val pdf: ByteArray,
    val standarder: Standarder,
)

data class Standard(
    val samsvarer: Boolean,
    val feiletRegel: String?,
)

data class Standarder(
    val ua1: Standard,
    val ua2: Standard,
    val `1a`: Standard,
    val `1b`: Standard,
    val `2a`: Standard,
    val `2b`: Standard,
    val `2u`: Standard,
    val `3a`: Standard,
    val `3b`: Standard,
    val `3u`: Standard,
    val `4`: Standard,
    val `4f`: Standard,
    val `4e`: Standard,
)
