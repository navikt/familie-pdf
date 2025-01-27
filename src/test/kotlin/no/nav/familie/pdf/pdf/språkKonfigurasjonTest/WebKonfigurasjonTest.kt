package no.nav.familie.pdf.no.nav.familie.pdf.pdf.språkContextTest

import io.mockk.mockk
import io.mockk.verify
import no.nav.familie.pdf.pdf.språkKonfigurasjon.SpråkAvlyttingskomponent
import no.nav.familie.pdf.pdf.språkKonfigurasjon.WebKonfigurasjon
import org.junit.jupiter.api.Test
import org.springframework.web.servlet.config.annotation.InterceptorRegistry

class WebKonfigurasjonTest {
    @Test
    fun `should add språkInterceptor to the registry`() {
        val språkAvlyttingskomponent: SpråkAvlyttingskomponent = mockk(relaxed = true)
        val interceptorRegistry: InterceptorRegistry = mockk(relaxed = true)

        val webKonfigurasjon = WebKonfigurasjon(språkAvlyttingskomponent)

        webKonfigurasjon.addInterceptors(interceptorRegistry)

        verify { interceptorRegistry.addInterceptor(språkAvlyttingskomponent) }
    }
}
