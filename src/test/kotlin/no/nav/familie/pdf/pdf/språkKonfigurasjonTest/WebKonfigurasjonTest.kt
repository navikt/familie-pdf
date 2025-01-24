package no.nav.familie.pdf.no.nav.familie.pdf.pdf.språkContextTest
import io.mockk.*
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkAvlyttingskomponent
import no.nav.familie.pdf.pdf.språkKonfigurasjon.WebKonfigurasjon
import org.junit.jupiter.api.Test
import org.springframework.web.servlet.config.annotation.InterceptorRegistry

class WebKonfigurasjonTest {
    @Test
    fun `should add språkInterceptor to the registry`() {
        // Mocking the InterceptorRegistry and SpråkInterceptor
        val språkAvlyttingskomponent: SpråkAvlyttingskomponent = mockk(relaxed = true)
        val interceptorRegistry: InterceptorRegistry = mockk(relaxed = true)

        // Create the WebConfig instance
        val webKonfigurasjon = WebKonfigurasjon(språkAvlyttingskomponent)

        // Call the method to test
        webKonfigurasjon.addInterceptors(interceptorRegistry)

        // Verify that the addInterceptor method was called with the correct argument
        verify { interceptorRegistry.addInterceptor(språkAvlyttingskomponent) }
    }
}
