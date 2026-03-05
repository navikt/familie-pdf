package no.nav.familie.pdf.pdf.lokalKjøring

import no.nav.familie.pdf.pdf.domain.FeltMap
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.io.FileNotFoundException
import java.io.IOException

object JsonLeser {
    private val jsonMapper: JsonMapper =
        jacksonMapperBuilder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

    fun lesSøknadJson(): FeltMap = lesSøknadJson("/søknad.json")

    fun lesSøknadJson(filNavn: String): FeltMap {
        val jsonInputStream =
            this::class.java.getResourceAsStream(filNavn)
                ?: throw FileNotFoundException("Kan ikke lese $filNavn")

        return try {
            jsonInputStream.bufferedReader().use { reader ->
                val result = jsonMapper.readValue(reader, FeltMap::class.java)
                result ?: throw ClassCastException("Uventet Json-format")
            }
        } catch (e: IOException) {
            throw RuntimeException("Feil ved lesing av JSON-fil", e)
        }
    }
}
