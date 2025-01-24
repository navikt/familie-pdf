import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkAvlyttingskomponent
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkKontekst
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.assertEquals

class SpråkAvlyttingskomponentTest {
    private lateinit var språkAvlyttingskomponent: SpråkAvlyttingskomponent
    private lateinit var objektMapper: ObjectMapper
    private lateinit var forespørsel: HttpServletRequest
    private lateinit var respons: HttpServletResponse

    @BeforeEach
    fun settOpp() {
        objektMapper = mockk()
        forespørsel = MockHttpServletRequest()
        respons = MockHttpServletResponse()
        språkAvlyttingskomponent = SpråkAvlyttingskomponent(objektMapper)
    }

    @AfterEach
    fun tilbakestill() {
        unmockkObject(SpråkKontekst)
    }

    @Test
    fun `preHandle setter språk korrekt`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkKontekst)

        // Forbered mock svar fra objektMapper.readTree
        val mockJsonNode = mockk<JsonNode>()
        every { mockJsonNode.get("pdfConfig")?.get("språk")?.asText() } returns "en"

        // Mock objektMapper
        every { objektMapper.readTree(forespørsel.inputStream) } returns mockJsonNode

        // Kall preHandle
        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        // Verifiser at riktig språk blir satt i SpråkContext
        verify { SpråkKontekst.settSpråk("en") }
        assertEquals(true, resultat)
    }

    @Test
    fun `preHandle setter standard språk til nb hvis ingen språk i body`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkKontekst)

        // Forbered mock svar fra objektMapper.readTree
        val mockJsonNode = mockk<JsonNode>()
        every { mockJsonNode.get("pdfConfig")?.get("språk")?.asText() } returns null

        // Mock objektMapper
        every { objektMapper.readTree(forespørsel.inputStream) } returns mockJsonNode

        // Kall preHandle
        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        // Verifiser at språket settes til standard "nb"
        verify { SpråkKontekst.settSpråk("nb") }
        assertEquals(true, resultat)
    }

    @Test
    fun `preHandle gjør ingenting når det er en feil ved parsing av JSON`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkKontekst)

        // Simuler en unntak ved parsing av JSON
        every { objektMapper.readTree(forespørsel.inputStream) } throws Exception("Ugyldig JSON")

        // Kall preHandle
        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        // Verifiser at språket settes til standard "nb"
        verify { SpråkKontekst.settSpråk("nb") }
        assertEquals(true, resultat)
    }

    @Test
    fun `afterCompletion fjerner språk`() {
        // Mocking av SpråkContext for å verifisere interaksjon
        mockkObject(SpråkKontekst)

        // Kall afterCompletion
        språkAvlyttingskomponent.afterCompletion(forespørsel, respons, Any(), null)

        // Verifiser at språket blir fjernet
        verify { SpråkKontekst.tilbakestillSpråk() }
    }
}
