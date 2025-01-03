package no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils

import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.PdfConfig
import no.nav.familie.pdf.pdf.domain.Sprak
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.domain.VisningsVariant

private val søknadsTittel = "Søknad om overgangsstønad (NAV 15-00.01)"

fun lagMedTomVerdiliste(): FeltMap = FeltMap(søknadsTittel, emptyList())

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
    )

fun lagMedTomAdresse(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Søker",
                    verdiliste =
                        listOf(
                            VerdilisteElement(label = "Adresse", verdi = ""),
                        ),
                ),
            ),
    )

fun lagAdresseMedBareLinjeskift(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Søker",
                    verdiliste =
                        listOf(
                            VerdilisteElement(label = "Adresse", verdi = "\n\n\n\n"),
                        ),
                ),
            ),
    )

fun lagAdresseMedFlereLinjeskift(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Søker",
                    verdiliste =
                        listOf(
                            VerdilisteElement(label = "Adresse", verdi = "Adresse 12\n\n\n\n0999 Oslo"),
                        ),
                ),
            ),
    )

fun lagToSiderInnholdsfortegnelse(): FeltMap = FeltMap(søknadsTittel, lagGjentattInnhold(48))

private fun lagGjentattInnhold(antallGanger: Int): List<VerdilisteElement> =
    List(antallGanger) { indeks ->
        VerdilisteElement(
            label = "Innsendingsdetaljer${if (indeks > 0) indeks + 1 else ""}",
            verdiliste = emptyList(),
        )
    }

fun lagMedFlereArbeidsforhold(): FeltMap =
    FeltMap(
        label = "Arbeid, utdanning og andre aktiviteter",
        verdiliste =
            listOf(
                VerdilisteElement(
                    label = "Hvordan er situasjonen din?",
                    verdi = "Jeg er arbeidstaker (og/eller lønnsmottaker som frilanser)",
                ),
                VerdilisteElement(
                    label = "Om arbeidsforholdet ditt",
                    visningsVariant = VisningsVariant.TABELL_ARBEIDSFORHOLD.toString(),
                    verdiliste =
                        listOf(
                            VerdilisteElement(label = "Navn på arbeidssted", verdi = "Norge.as"),
                            VerdilisteElement(label = "Navn på arbeidssted", verdi = "Sverige.as"),
                        ),
                ),
            ),
    )

fun lagUteninnholdsfortegnelse(): FeltMap =
    FeltMap(
        label = søknadsTittel,
        pdfConfig =
            PdfConfig(
                harInnholdsFortegnelse = false,
                sprak = Sprak.NO,
            ),
        verdiliste = lagGjentattInnhold(20),
    )
