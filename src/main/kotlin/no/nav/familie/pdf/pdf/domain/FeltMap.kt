package no.nav.familie.pdf.pdf.domain

import jakarta.validation.constraints.NotNull

data class FeltMap(
    @field:NotNull(message = "Label kan ikke være null")
    val label: String,
    @field:NotNull(message = "Verdiliste kan ikke være null")
    val verdiliste: List<VerdilisteElement>,
)

data class VerdilisteElement(
    val label: String,
    val verdi: String? = null,
    val visningsVariant: String? = null,
    val verdiliste: List<VerdilisteElement>? = null,
    val alternativer: String? = null,
)
