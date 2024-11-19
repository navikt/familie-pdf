package no.nav.familie.pdf.pdf

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.FileNotFoundException

val objectMapper: ObjectMapper =
    jacksonObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .registerModule(JavaTimeModule())

fun lesJSON(): Map<String, Any> {
    val inndataStreng =
        object {}
            .javaClass
            .getResourceAsStream("/søknad.json")
            ?.bufferedReader()
            ?.readText()
            ?: throw FileNotFoundException("Kan ikke lese søknad.json")
    return objectMapper.readValue(inndataStreng, Map::class.java) as Map<String, Any>
}
