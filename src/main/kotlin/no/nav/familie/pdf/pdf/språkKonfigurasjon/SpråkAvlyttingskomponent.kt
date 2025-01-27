package no.nav.familie.pdf.pdf.språkKonfigurasjon

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.io.IOException

@Component
class SpråkAvlyttingskomponent(
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Throws(Exception::class)
    override fun preHandle(
        forespørsel: HttpServletRequest,
        respons: HttpServletResponse,
        håndterer: Any,
    ): Boolean {
        val språk = hentSpråkFraBody(forespørsel) ?: "nb"
        SpråkKontekst.settSpråk(språk)
        return true
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        forespørsel: HttpServletRequest,
        respons: HttpServletResponse,
        håndterer: Any,
        unntak: Exception?,
    ) {
        SpråkKontekst.tilbakestillSpråk()
    }

    private fun hentSpråkFraBody(forespørsel: HttpServletRequest): String? =
        try {
            val jsonNode = objectMapper.readTree(forespørsel.inputStream)
            jsonNode.get("pdfConfig")?.get("språk")?.asText()
        } catch (e: IOException) {
            logger.warn("I/O-feil ved lesing av request body", e)
            null
        } catch (e: JsonProcessingException) {
            logger.warn("Feil ved parsing av JSON", e)
            null
        } catch (e: Exception) {
            logger.warn("Kunne ikke sette språk grunnet unntak:", e)
            null
        }
}
