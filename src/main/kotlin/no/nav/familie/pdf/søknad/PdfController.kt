package no.nav.familie.pdf.søknad

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/pdf")
@Unprotected
class PdfController {
    @GetMapping("/helsesjekk")
    fun lagPdfForOvergangsstønad(): String = "OK - overgangsstønad"
}
