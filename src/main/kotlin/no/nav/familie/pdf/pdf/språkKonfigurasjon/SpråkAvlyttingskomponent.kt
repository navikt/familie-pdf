package no.nav.familie.pdf.pdf.språkKonfigurasjon

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class SpråkAvlyttingskomponent(
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {
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
        } catch (e: Exception) {
            null
        }
}
