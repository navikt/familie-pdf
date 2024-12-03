package no.nav.familie.pdf.søknad

import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/soknad")
@ProtectedWithClaims(
    issuer = "azuread",
)
class SøknadController {
    @GetMapping("/overgangsstonad")
    fun lagPdfForOvergangsstønad(): String = "OK - overgangsstønad"
}
