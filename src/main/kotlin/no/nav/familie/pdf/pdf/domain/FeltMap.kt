package no.nav.familie.pdf.pdf.domain

data class FeltMap(
    val label: String,
    val verdiliste: List<VerdilisteElement>,
    val pdfConfig: PdfConfig,
    val skjemanummer: String? = null,
    val bunntekst: EkstraBunntekst? = null,
)

data class VerdilisteElement(
    val label: String,
    val verdi: String? = null,
    val visningsVariant: String? = null,
    val verdiliste: List<VerdilisteElement>? = null,
)

data class PdfConfig(
    val harInnholdsfortegnelse: Boolean,
    val språk: String
)

data class EkstraBunntekst(
    val upperleft: String? = null,
    val lowerleft: String? = null,
    val upperMiddle: String? = null,
    val lowerMiddle: String? = null,
    val upperRight: String? = null,
)