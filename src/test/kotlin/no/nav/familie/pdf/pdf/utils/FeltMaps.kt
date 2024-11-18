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
