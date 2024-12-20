package no.nav.familie.pdf.pdf.domain

data class FeltMap(
    val label: String,
    val verdiliste: List<VerdilisteElement>,
)

data class VerdilisteElement(
    val label: String,
    val verdi: String? = null,
    val visningsVariant: String? = null,
    val verdiliste: List<VerdilisteElement>? = null,
    val alternativer: String? = null,
)
