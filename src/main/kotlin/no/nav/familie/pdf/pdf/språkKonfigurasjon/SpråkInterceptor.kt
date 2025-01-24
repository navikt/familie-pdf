package no.nav.familie.pdf.pdf.språkKonfigurasjon

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class SpråkInterceptor(
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val språk = extractLanguageFromBody(request) ?: "nb"
        SpråkKontekst.setSpråk(språk)
        return true
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        SpråkKontekst.fjernSpråk()
    }

    private fun extractLanguageFromBody(request: HttpServletRequest): String? =
        try {
            val jsonNode = objectMapper.readTree(request.inputStream)
            jsonNode.get("pdfConfig")?.get("språk")?.asText()
        } catch (e: Exception) {
            null
        }
}
