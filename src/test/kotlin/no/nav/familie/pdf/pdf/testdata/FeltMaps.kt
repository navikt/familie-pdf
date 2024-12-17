package no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils

import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.VerdilisteItem
import no.nav.familie.pdf.pdf.domain.VisningsVariant

private val søknadsTittel = "Søknad om overgangsstønad (NAV 15-00.01)"

fun lagMedTomVerdiliste(): FeltMap = FeltMap(søknadsTittel, emptyList())

fun lagMedVerdiliste(): FeltMap =
    FeltMap(
        label = "Søknad om overgangsstønad",
        verdiliste =
            listOf(
                VerdilisteItem(
                    label = "Innsendingsdetaljer",
                    verdiliste =
                        listOf(
                            VerdilisteItem(label = "Navn", verdi = "Kåre"),
                            VerdilisteItem(label = "Født", verdi = "Ja"),
                        ),
                ),
            ),
    )

fun lagMedForskjelligLabelIVerdiliste(): FeltMap =
    FeltMap(
        label = "Søknad om overgangsstønad",
        verdiliste =
            listOf(
                VerdilisteItem(
                    label = "Barna dine",
                    verdiliste = emptyList(),
                ),
                VerdilisteItem(
                    label = "Innsendingsdetaljer",
                    verdiliste =
                        listOf(
                            VerdilisteItem(label = "Navn", verdi = "Bjarne"),
                        ),
                ),
            ),
    )

fun lagMedTomAdresse(): FeltMap =
    FeltMap(
        label = "Søknad om overgangsstønad",
        verdiliste =
            listOf(
                VerdilisteItem(
                    label = "Søker",
                    verdiliste =
                        listOf(
                            VerdilisteItem(label = "Adresse", verdi = ""),
                        ),
                ),
            ),
    )

fun lagAdresseMedBareLinjeskift(): FeltMap =
    FeltMap(
        label = "Søknad om overgangsstønad",
        verdiliste =
            listOf(
                VerdilisteItem(
                    label = "Søker",
                    verdiliste =
                        listOf(
                            VerdilisteItem(label = "Adresse", verdi = "\n\n\n\n"),
                        ),
                ),
            ),
    )

fun lagAdresseMedFlereLinjeskift(): FeltMap =
    FeltMap(
        label = "Søknad om overgangsstønad",
        verdiliste =
            listOf(
                VerdilisteItem(
                    label = "Søker",
                    verdiliste =
                        listOf(
                            VerdilisteItem(label = "Adresse", verdi = "Adresse 12\n\n\n\n0999 Oslo"),
                        ),
                ),
            ),
    )

fun lagToSiderInnholdsfortegnelse(): FeltMap = FeltMap(søknadsTittel, lagGjentattInnhold(48))

private fun lagGjentattInnhold(antallGanger: Int): List<VerdilisteItem> =
    List(antallGanger) { indeks ->
        VerdilisteItem(
            label = "Innsendingsdetaljer${if (indeks > 0) indeks + 1 else ""}",
            verdiliste = emptyList(),
        )
    }

fun lagMedFlereArbeidsforhold(): FeltMap =
    FeltMap(
        label = "Arbeid, utdanning og andre aktiviteter",
        verdiliste =
            listOf(
                VerdilisteItem(
                    label = "Hvordan er situasjonen din?",
                    verdi = "Jeg er arbeidstaker (og/eller lønnsmottaker som frilanser)",
                ),
                VerdilisteItem(
                    label = "Om arbeidsforholdet ditt",
                    visningsVariant = VisningsVariant.TABELL_ARBEIDSFORHOLD.toString(),
                    verdiliste =
                        listOf(
                            VerdilisteItem(label = "Navn på arbeidssted", verdi = "Norge.as"),
                            VerdilisteItem(label = "Navn på arbeidssted", verdi = "Sverige.as"),
                        ),
                ),
            ),
    )
