package no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils

import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.PdfConfig
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.domain.VisningsVariant

private val søknadsTittel = "Søknad om overgangsstønad (NAV 15-00.01)"

//region Pdf
fun lagMedTomVerdiliste(): FeltMap = FeltMap(søknadsTittel, emptyList(), PdfConfig(true, "nb"))

fun lagMedVerdiliste(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Innsendingsdetaljer",
                    verdiliste =
                        listOf(
                            VerdilisteElement(label = "Navn", verdi = "Kåre"),
                            VerdilisteElement(label = "Født", verdi = "Ja"),
                        ),
                ),
            ),
        pdfConfig = PdfConfig(true, "nb"),
    )

fun lagMedForskjelligLabelIVerdiliste(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Barna dine",
                    verdiliste = emptyList(),
                ),
                VerdilisteElement(
                    label = "Innsendingsdetaljer",
                    verdiliste =
                        listOf(
                            VerdilisteElement(label = "Navn", verdi = "Bjarne"),
                        ),
                ),
            ),
        pdfConfig = PdfConfig(true, "nb"),
    )
//endregion

//region Innholdsfortegnelse
fun lagToSiderInnholdsfortegnelse(): FeltMap = FeltMap(søknadsTittel, lagGjentattInnhold(48), pdfConfig = PdfConfig(true, "nb"))

private fun lagGjentattInnhold(antallGanger: Int): List<VerdilisteElement> =
    List(antallGanger) { indeks ->
        VerdilisteElement(
            label = "Innsendingsdetaljer${if (indeks > 0) indeks + 1 else ""}",
            verdiliste = emptyList(),
        )
    }
//endregion

//region Tabeller
fun lagMedFlereArbeidsforhold(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Arbeid, utdanning og andre aktiviteter",
                    verdiliste =
                        listOf(
                            VerdilisteElement(
                                label = "Hvordan er situasjonen din?",
                                verdi = "Jeg er arbeidstaker (og/eller lønnsmottaker som frilanser)",
                            ),
                            VerdilisteElement(
                                label = "Om arbeidsforholdet ditt",
                                visningsVariant = VisningsVariant.TABELL.toString(),
                                verdiliste =
                                    listOf(
                                        VerdilisteElement(
                                            label = "Arbeidsforhold 1",
                                            verdiliste =
                                                listOf(
                                                    VerdilisteElement(
                                                        label = "Navn på arbeidssted",
                                                        verdi = "Nav",
                                                    ),
                                                ),
                                        ),
                                        VerdilisteElement(
                                            label = "Arbeidsforhold 2",
                                            verdiliste =
                                                listOf(
                                                    VerdilisteElement(
                                                        label = "Navn på arbeidssted",
                                                        verdi = "Bekk",
                                                    ),
                                                ),
                                        ),
                                    ),
                            ),
                        ),
                ),
            ),
        pdfConfig = PdfConfig(true, "nb"),
    )

fun lagMedBarneTabell(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Barna dine",
                    visningsVariant = VisningsVariant.TABELL.toString(),
                    verdiliste =
                        listOf(
                            VerdilisteElement(
                                label = "Barn 1",
                                verdiliste =
                                    listOf(
                                        VerdilisteElement(label = "Navn", verdi = "Kåre"),
                                    ),
                            ),
                            VerdilisteElement(
                                label = "Barn 2",
                                verdiliste =
                                    listOf(
                                        VerdilisteElement(label = "Navn", verdi = ""),
                                        VerdilisteElement(label = "Termindato", verdi = "2022-01-01"),
                                    ),
                            ),
                        ),
                ),
            ),
        pdfConfig = PdfConfig(true, "nb"),
    )

fun lagUteninnholdsfortegnelse(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste = lagGjentattInnhold(20),
        pdfConfig =
            PdfConfig(
                harInnholdsfortegnelse = false,
                "nb",
            ),
    )

fun lagMedInnholdsfortegnelse(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste = lagGjentattInnhold(20),
        pdfConfig =
            PdfConfig(
                harInnholdsfortegnelse = true,
                "nb",
            ),
    )
//endregion

// region Punktliste
fun lagMedPunktliste(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Mer om situasjonen din",
                    verdiliste =
                        listOf(
                            VerdilisteElement(
                                label = "Gjelder noe av dette deg?",
                                visningsVariant = VisningsVariant.PUNKTLISTE.toString(),
                                verdiliste =
                                    listOf(
                                        VerdilisteElement(
                                            label = "Jeg er syk",
                                            verdi = "true",
                                        ),
                                        VerdilisteElement(
                                            label = "Barnet mitt er sykt",
                                            verdi = "true",
                                        ),
                                        VerdilisteElement(
                                            label = "Jeg har søkt om barnepass",
                                            verdi = "false",
                                        ),
                                    ),
                            ),
                        ),
                ),
            ),
        pdfConfig = PdfConfig(true, språk = "nb"),
    )

fun lagMedTomPunktliste(punktliste: List<VerdilisteElement>? = null): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Mer om situasjonen din",
                    verdiliste =
                        listOf(
                            VerdilisteElement(
                                label = "Gjelder noe av dette deg?",
                                visningsVariant = VisningsVariant.PUNKTLISTE.toString(),
                                verdiliste = punktliste,
                            ),
                        ),
                ),
            ),
        pdfConfig = PdfConfig(true, språk = "nb"),
    )
// endregion
