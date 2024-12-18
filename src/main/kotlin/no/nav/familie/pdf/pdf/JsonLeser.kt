package no.nav.familie.pdf.pdf

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.familie.pdf.pdf.domain.FeltMap
import java.io.FileNotFoundException
import java.io.IOException

object JsonLeser {
    private val objectMapper: ObjectMapper =
        jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(JavaTimeModule())

    fun lesSøknadJson(): FeltMap {
        val jsonInputStream =
            this::class.java.getResourceAsStream("/søknad.json")
                ?: throw FileNotFoundException("Kan ikke lese søknad.json")

        return try {
            jsonInputStream.bufferedReader().use { reader ->
                val result = objectMapper.readValue(reader, FeltMap::class.java)
                result ?: throw ClassCastException("Uventet Json-format")
            }
        } catch (e: IOException) {
            throw RuntimeException("Feil ved lesing av JSON-fil", e)
        }
    }
}
