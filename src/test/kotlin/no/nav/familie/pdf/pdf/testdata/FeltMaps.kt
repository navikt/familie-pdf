package no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils

fun lagMedTomVerdiliste(): Map<String, Any> = mapOf("label" to "Søknad om overgangsstønad", "verdiliste" to emptyList<Any>())

fun lagMedVerdiliste(): Map<String, Any> =
    mapOf(
        "label" to "Søknad om overgangsstønad",
        "verdiliste" to
            listOf(
                mapOf(
                    "label" to "Innsendingsdetaljer",
                    "verdiliste" to
                        listOf(
                            mapOf("label" to "Navn", "verdi" to "Kåre"),
                            mapOf("label" to "Født", "verdi" to "Ja"),
                        ),
                ),
            ),
    )

fun lagMedForskjelligLabelIVerdiliste(): Map<String, Any> =
    mapOf(
        "label" to "Søknad om overgangsstønad",
        "verdiliste" to
            listOf(
                mapOf("label" to "Barna dine", "verdiliste" to emptyList<Any>()),
                mapOf(
                    "label" to "Innsendingsdetaljer",
                    "verdiliste" to listOf(mapOf("label" to "Navn", "verdi" to "Bjarne")),
                ),
            ),
    )

fun lagNullInnhold(): Map<String, Any?> = mapOf("label" to null, "verdiliste" to null)

fun lagMedTomAdresse(): Map<String, Any> =
    mapOf(
        "label" to "Søknad om overgangsstønad",
        "verdiliste" to
            listOf(
                mapOf(
                    "label" to "Søker",
                    "verdiliste" to listOf(mapOf("label" to "Adresse", "verdi" to "")),
                ),
            ),
    )

fun lagAdresseMedBareLinjeskift(): Map<String, Any> =
    mapOf(
        "label" to "Søknad om overgangsstønad",
        "verdiliste" to
            listOf(
                mapOf(
                    "label" to "Søker",
                    "verdiliste" to listOf(mapOf("label" to "Adresse", "verdi" to "\n\n\n\n")),
                ),
            ),
    )

fun lagAdresseMedFlereLinjeskift(): Map<String, Any> =
    mapOf(
        "label" to "Søknad om overgangsstønad",
        "verdiliste" to
            listOf(
                mapOf(
                    "label" to "Søker",
                    "verdiliste" to listOf(mapOf("label" to "Adresse", "verdi" to "Adresse 12\n\n\n\n0999 Oslo")),
                ),
            ),
    )

fun lagToSiderInnholdsfortegnelse(): Map<String, Any> =
    mapOf(
        "label" to "Søknad om overgangsstønad (NAV 15-00.01)",
        "verdiliste" to lagGjentattInnhold(48),
    )

private fun lagGjentattInnhold(antallGanger: Int): List<Map<String, Any>> =
    List(antallGanger) { indeks ->
        mapOf(
            "label" to "Innsendingsdetaljer${if (indeks > 0) indeks + 1 else ""}",
            "verdiliste" to emptyList<Any>(),
        )
    }

fun lagMedFlereArbeidsforhold(): Map<String, Any> =
    mapOf(
        "label" to "Arbeid, utdanning og andre aktiviteter",
        "verdiliste" to
            listOf(
                mapOf("label" to "Hvordan er situasjonen din?", "verdi" to "Jeg er arbeidstaker (og/eller lønnsmottaker som frilanser)"),
                mapOf(
                    "label" to "Om arbeidsforholdet ditt",
                    "visningsVariant" to "TABELL_ARBEIDSFORHOLD",
                    "verdiliste" to
                        listOf(
                            mapOf("label" to "Navn på arbeidssted", "verdi" to "Norge.as"),
                            mapOf("label" to "Navn på arbeidssted", "verdi" to "Sverige.as"),
                        ),
                ),
            ),
    )
