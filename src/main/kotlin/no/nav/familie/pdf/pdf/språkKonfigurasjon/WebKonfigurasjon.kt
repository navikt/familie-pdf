package no.nav.familie.pdf.pdf.språkKonfigurasjon

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebKonfigurasjon(
    private val språkAvlyttingskomponent: SpråkAvlyttingskomponent,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(språkAvlyttingskomponent)
    }
}
