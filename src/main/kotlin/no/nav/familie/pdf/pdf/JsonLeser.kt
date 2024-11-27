package no.nav.familie.pdf.pdf

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.FileNotFoundException
import java.io.IOException

object JsonLeser {
    private val objectMapper: ObjectMapper =
        jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(JavaTimeModule())

    fun lesSøknadJson(): Map<String, Any> {
        val jsonInputStream =
            this::class.java.getResourceAsStream("/søknad.json")
                ?: throw FileNotFoundException("Kan ikke lese søknad.json")

        return try {
            jsonInputStream.bufferedReader().use { reader ->
                val result = objectMapper.readValue(reader, Map::class.java)
                result as? Map<String, Any> ?: throw ClassCastException("Unexpected JSON format")
            }
        } catch (e: IOException) {
            throw RuntimeException("Feil ved lesing av JSON-fil", e)
        }
    }
}
