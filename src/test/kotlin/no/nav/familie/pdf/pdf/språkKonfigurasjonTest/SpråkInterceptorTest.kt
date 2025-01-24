import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkContext
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkInterceptor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.assertEquals

class SpråkInterceptorTest {
    private lateinit var språkInterceptor: SpråkInterceptor
    private lateinit var objektMapper: ObjectMapper
    private lateinit var forespørsel: HttpServletRequest
    private lateinit var respons: HttpServletResponse

    @BeforeEach
    fun settOpp() {
        objektMapper = mockk()
        forespørsel = MockHttpServletRequest()
        respons = MockHttpServletResponse()
        språkInterceptor = SpråkInterceptor(objektMapper)
    }

    @Test
    fun `preHandle setter språk korrekt`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkContext)

        // Forbered mock svar fra objektMapper.readTree
        val mockJsonNode = mockk<JsonNode>()
        every { mockJsonNode.get("pdfConfig")?.get("språk")?.asText() } returns "en"

        // Mock objektMapper
        every { objektMapper.readTree(forespørsel.inputStream) } returns mockJsonNode

        // Kall preHandle
        val resultat = språkInterceptor.preHandle(forespørsel, respons, Any())

        // Verifiser at riktig språk blir satt i SpråkContext
        verify { SpråkContext.setSpråk("en") }
        assertEquals(true, resultat)

        // Unmock SpråkContext etter bruk
        unmockkObject(SpråkContext)
    }

    @Test
    fun `preHandle setter standard språk til nb hvis ingen språk i body`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkContext)

        // Forbered mock svar fra objektMapper.readTree
        val mockJsonNode = mockk<JsonNode>()
        every { mockJsonNode.get("pdfConfig")?.get("språk")?.asText() } returns null

        // Mock objektMapper
        every { objektMapper.readTree(forespørsel.inputStream) } returns mockJsonNode

        // Kall preHandle
        val resultat = språkInterceptor.preHandle(forespørsel, respons, Any())

        // Verifiser at språket settes til standard "nb"
        verify { SpråkContext.setSpråk("nb") }
        assertEquals(true, resultat)

        // Unmock SpråkContext etter bruk
        unmockkObject(SpråkContext)
    }

    @Test
    fun `preHandle gjør ingenting når det er en feil ved parsing av JSON`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkContext)

        // Simuler en unntak ved parsing av JSON
        every { objektMapper.readTree(forespørsel.inputStream) } throws Exception("Ugyldig JSON")

        // Kall preHandle
        val resultat = språkInterceptor.preHandle(forespørsel, respons, Any())

        // Verifiser at språket settes til standard "nb"
        verify { SpråkContext.setSpråk("nb") }
        assertEquals(true, resultat)

        // Unmock SpråkContext etter bruk
        unmockkObject(SpråkContext)
    }

    @Test
    fun `afterCompletion fjerner språk`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkContext)

        // Kall afterCompletion
        språkInterceptor.afterCompletion(forespørsel, respons, Any(), null)

        // Verifiser at språket blir fjernet
        verify { SpråkContext.fjernSpråk() }

        // Unmock SpråkContext etter bruk
        unmockkObject(SpråkContext)
    }
}
