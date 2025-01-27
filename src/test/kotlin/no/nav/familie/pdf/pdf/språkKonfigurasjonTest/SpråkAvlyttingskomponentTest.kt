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
import java.io.IOException
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
        mockkObject(SpråkKontekst)
        val mockJsonNode = mockk<JsonNode>()
        every { mockJsonNode.get("pdfConfig")?.get("språk")?.asText() } returns "en"
        every { objektMapper.readTree(forespørsel.inputStream) } returns mockJsonNode

        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        verify { SpråkKontekst.settSpråk("en") }
        assertEquals(true, resultat)
    }

    @Test
    fun `preHandle setter standard språk til nb hvis ingen språk i body`() {
        mockkObject(SpråkKontekst)
        val mockJsonNode = mockk<JsonNode>()
        every { mockJsonNode.get("pdfConfig")?.get("språk")?.asText() } returns null
        every { objektMapper.readTree(forespørsel.inputStream) } returns mockJsonNode

        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        verify { SpråkKontekst.settSpråk("nb") }
        assertEquals(true, resultat)
    }

    @Test
    fun `preHandle setter standard språk ved IOException`() {
        mockkObject(SpråkKontekst)
        every { objektMapper.readTree(forespørsel.inputStream) } throws IOException("I/O feil")

        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        verify { SpråkKontekst.settSpråk("nb") }
        assertEquals(true, resultat)
    }

    @Test
    fun `preHandle setter standard språk ved JsonProcessingException`() {
        mockkObject(SpråkKontekst)
        every { objektMapper.readTree(forespørsel.inputStream) } throws
            com.nimbusds.jose.shaded.gson.JsonParseException(
                "JSON parsing feil",
                null,
            )
        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        verify { SpråkKontekst.settSpråk("nb") }
        assertEquals(true, resultat)
    }

    @Test
    fun `preHandle setter standard språk ved generell Exception`() {
        mockkObject(SpråkKontekst)
        every { objektMapper.readTree(forespørsel.inputStream) } throws Exception("Uventet feil")

        val resultat = språkAvlyttingskomponent.preHandle(forespørsel, respons, Any())

        verify { SpråkKontekst.settSpråk("nb") }
        assertEquals(true, resultat)
    }

    @Test
    fun `afterCompletion fjerner språk`() {
        mockkObject(SpråkKontekst)

        språkAvlyttingskomponent.afterCompletion(forespørsel, respons, Any(), null)

        verify { SpråkKontekst.tilbakestillSpråk() }
    }
}
