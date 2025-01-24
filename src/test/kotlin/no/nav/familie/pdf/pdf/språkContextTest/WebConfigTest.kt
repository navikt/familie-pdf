package no.nav.familie.pdf.no.nav.familie.pdf.pdf.språkContextTest
import io.mockk.*
import no.nav.familie.pdf.pdf.språkContext.SpråkInterceptor
import no.nav.familie.pdf.pdf.språkContext.WebConfig
import org.junit.jupiter.api.Test
import org.springframework.web.servlet.config.annotation.InterceptorRegistry

class WebConfigTest {
    @Test
    fun `should add språkInterceptor to the registry`() {
        // Mocking the InterceptorRegistry and SpråkInterceptor
        val språkInterceptor: SpråkInterceptor = mockk(relaxed = true)
        val interceptorRegistry: InterceptorRegistry = mockk(relaxed = true)

        // Create the WebConfig instance
        val webConfig = WebConfig(språkInterceptor)

        // Call the method to test
        webConfig.addInterceptors(interceptorRegistry)

        // Verify that the addInterceptor method was called with the correct argument
        verify { interceptorRegistry.addInterceptor(språkInterceptor) }
    }
}
